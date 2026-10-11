package studio.hunter.sanatorio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SanatorioAudioBridge {
  private static final String TAG="SanatorioAudio";
  private final Handler ui=new Handler(Looper.getMainLooper());
  private final AssetManager assets;
  private SoundPool pool;
  private final LinkedHashMap<String,Integer> cache=new LinkedHashMap<>(64,.75f,true);
  private final Map<Integer,List<Pending>> pending=new HashMap<>();
  private final MediaPlayer[] music=new MediaPlayer[2];
  private MediaPlayer ambience;
  private int activeSlot=0,track=-1,musicRequest=0;
  private boolean playing=false, pausedByApp=false, released=false;
  private float musicVol=.42f,sfxVol=.9f,ambientVol=.1f;
  private static final int LIMIT=48;
  private static final class Pending {
    final float volume,pan,rate;
    Pending(float v,float p,float r){volume=v;pan=p;rate=r;}
  }

  SanatorioAudioBridge(Context context){
    assets=context.getAssets();
    AudioAttributes aa=new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
      .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
    pool=new SoundPool.Builder().setAudioAttributes(aa).setMaxStreams(20).build();
    pool.setOnLoadCompleteListener((p,id,status)->ui.post(()->{
      List<Pending> jobs=pending.remove(id);
      if(status!=0){Log.e(TAG,"SFX failed id="+id+" status="+status);return;}
      if(jobs!=null)for(Pending q:jobs)playLoaded(id,q);
    }));
  }
  private static float limit(double value){return (float)Math.max(0,Math.min(1,value));}
  private static boolean safe(String value){return value!=null&&value.matches("[a-zA-Z0-9_-]{1,100}");}
  private static AudioAttributes musicAttrs(){return new AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();}
  private void playLoaded(int id,Pending a){
    if(released||pausedByApp)return;
    float v=limit(a.volume)*sfxVol;
    float pan=Math.max(-1,Math.min(1,a.pan));
    float left=v*(pan>0?1-pan:1),right=v*(pan<0?1+pan:1);
    pool.play(id,left,right,1,0,Math.max(.5f,Math.min(2,a.rate)));
  }
  @JavascriptInterface public void playSfx(String name,double volume,double pan,double speed) {
    if(!safe(name))return;
    Pending item=new Pending(limit(volume),(float)pan,(float)speed);
    ui.post(()->{
      if(released||pausedByApp)return;
      Integer loaded=cache.get(name);
      if(loaded!=null){
        if(pending.containsKey(loaded))pending.get(loaded).add(item);
        else playLoaded(loaded,item);
        return;
      }
      try(AssetFileDescriptor fd=assets.openFd("game/assets/"+name+".wav")){
        int id=pool.load(fd,1);
        if(id==0){Log.e(TAG,"SoundPool.load returned zero: "+name);return;}
        cache.put(name,id);
        List<Pending> jobs=new ArrayList<>();
        jobs.add(item);pending.put(id,jobs);
        if(cache.size()>LIMIT){
          String victim=cache.keySet().iterator().next();
          int victimId=cache.remove(victim);
          pending.remove(victimId);
          pool.unload(victimId);
        }
      }catch(Exception e){Log.e(TAG,"SFX "+name,e);}
    });
  }
  private MediaPlayer createMedia(String relative, boolean loop, int request, int slot){
    try {
      AssetFileDescriptor fd=assets.openFd("game/assets/"+relative);
      MediaPlayer m=new MediaPlayer();
      m.setAudioAttributes(musicAttrs());
      m.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());
      fd.close();
      m.setLooping(loop);
      m.setVolume(0,0);
      m.setOnPreparedListener(ready->{
        if(released || (slot>=0 && request!=musicRequest)){ready.release();return;}
        if(!pausedByApp && playing || slot<0){
          try{ready.start();}catch(Exception e){Log.e(TAG,"Media start "+relative,e);}
        }
        adjustVolumes();
      });
      m.setOnErrorListener((player,what,extra)->{
        Log.e(TAG,"MediaPlayer "+relative+" err "+what+" "+extra);return false;
      });
      m.prepareAsync();
      return m;
    } catch(Exception e){Log.e(TAG,"Unable to stream "+relative,e);return null;}
  }
  @JavascriptInterface public void begin(){
    ui.post(()->{
      if(released)return;
      pausedByApp=false;
      if(ambience==null){
        ambience=createMedia("ambient.wav",true,-1,-1);
      }
      if(ambience!=null && !ambience.isPlaying()){
        try{ambience.start();}catch(Exception ignored){}
      }
      adjustVolumes();
    });
  }
  @JavascriptInterface public void setTrack(int value,double gain,boolean enabled){
    final float requestedVolume=limit(gain);
    ui.post(()->{
      if(released)return;
      musicVol=requestedVolume;
      playing=enabled;
      if(!enabled){
        for(MediaPlayer m:music)if(m!=null&&m.isPlaying())try{m.pause();}catch(Exception ignored){}
        adjustVolumes();
        return;
      }
      if(value<0||value>12){adjustVolumes();return;}
      if(track!=value){
        int oldSlot=activeSlot;
        activeSlot=1-activeSlot;
        track=value;
        musicRequest++;
        if(music[activeSlot]!=null){try{music[activeSlot].release();}catch(Exception ignored){}}
        music[activeSlot]=createMedia(String.format(java.util.Locale.ROOT,"ost%02d.mp3",value),
          true,musicRequest,activeSlot);
        if(music[oldSlot]!=null){try{music[oldSlot].release();}catch(Exception ignored){} music[oldSlot]=null;}
      }else if(music[activeSlot]!=null&&!music[activeSlot].isPlaying()){
        try{music[activeSlot].start();}catch(IllegalStateException ignored){}
      }
      adjustVolumes();
    });
  }
  @JavascriptInterface public void setSfxVolume(double gain){
    ui.post(()->{sfxVol=limit(gain);adjustVolumes();});
  }
  @JavascriptInterface public void setAmbientVolume(double gain){
    ui.post(()->{ambientVol=limit(gain);adjustVolumes();});
  }
  private void adjustVolumes(){
    if(released)return;
    for(int i=0;i<2;i++)if(music[i]!=null)try{
      float volume=playing && i==activeSlot && !pausedByApp ? musicVol:0;
      music[i].setVolume(volume,volume);
    }catch(Exception ignored){}
    if(ambience!=null)try{
      float v=playing&&!pausedByApp?ambientVol*sfxVol:0;
      ambience.setVolume(v,v);
    }catch(Exception ignored){}
  }
  void onAppPause(){
    ui.post(()->{
      pausedByApp=true;
      for(MediaPlayer m:music)if(m!=null)try{if(m.isPlaying())m.pause();}catch(Exception ignored){}
      if(ambience!=null)try{if(ambience.isPlaying())ambience.pause();}catch(Exception ignored){}
    });
  }
  void onAppResume(){ui.post(()->{pausedByApp=false;if(ambience!=null)try{if(!ambience.isPlaying())ambience.start();}catch(Exception ignored){}adjustVolumes();});}
  void destroy(){
    ui.post(()->{
      released=true;
      for(int i=0;i<2;i++)if(music[i]!=null){try{music[i].release();}catch(Exception ignored){}music[i]=null;}
      if(ambience!=null){try{ambience.release();}catch(Exception ignored){}ambience=null;}
      if(pool!=null){pool.release();pool=null;}
      pending.clear();cache.clear();
    });
  }
}
