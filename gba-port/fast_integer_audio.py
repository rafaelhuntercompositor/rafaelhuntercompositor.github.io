from pathlib import Path
import math
p=Path("real8/src/core/real8_audio.cpp")
s=p.read_text()
sig="void AudioEngine::generateSamples(int16_t* out_buffer, int count) {"
assert s.count(sig)==1
inc=[min(4294967295,int(round(65.406*(2**(i/48))*4294967296/22050))) for i in range(256)]
table=",".join(str(v)+"u" for v in inc)
fast=r"""
    // ARM7TDMI has no FPU. Render with integer phase oscillators and update
    // note/effect parameters at 120 Hz rather than doing floats per PCM sample.
    static const uint32_t kGbaSteps[256]={PHASE_TABLE};
    static uint32_t phase[4]={0,0,0,0};
    static uint32_t delta[4]={0,0,0,0};
    static uint8_t kind[4]={0,0,0,0};
    static int16_t gain[4]={0,0,0,0};
    static unsigned sequencer=0;
    updateVolumeMute();
    if (isMuted()) {
        memset(out_buffer,0,count*sizeof(int16_t));
        return;
    }
    for(int i=0;i<count;++i){
        sequencer+=120u;
        bool fresh=(sequencer>=22050u);
        if(fresh){
            sequencer-=22050u;
            run_tick();
        }
        if(i==0 || fresh){
            for(int c=0;c<4;++c){
                Channel& ch=channels[c];
                if(ch.sfx_id<0){
                    delta[c]=0;
                    gain[c]=0;
                    continue;
                }
                uint8_t* data=vm->sfx_ram+(ch.sfx_id*68);
                int row=ch.last_note_idx<0?0:ch.last_note_idx;
                if(row>31) row=31;
                int pitch_key=(int)(data[row*2]&63);
                int hi=(int)data[row*2+1];
                kind[c]=(uint8_t)((hi>>5)&7);
                float pitch=ch.current_pitch_val;
                float progress=0.0f;
                if(ch.speed>0) progress=1.0f-((float)ch.tick_counter/(float)ch.speed);
                switch(ch.effect){
                    case 1: pitch=ch.slide_start_pitch+(float(pitch_key)-ch.slide_start_pitch)*progress;break;
                    case 2: {
                        ch.vib_phase+=g_vibPhaseStep;
                        if(ch.vib_phase>=1.0f)ch.vib_phase-=1.0f;
                        int idx=(int)(ch.vib_phase*256.0f)&255;
                        pitch+=(float)g_vibSinLut[idx]*(kVibLutScale*kVibAmp);
                        break;
                    }
                    case 3: pitch=ch.slide_start_pitch*(1.0f-progress);break;
                    default:break;
                }
                int quant=(int)(pitch*4.0f+0.5f);
                if(quant<0)quant=0;
                if(quant>252)quant=252;
                delta[c]=kGbaSteps[quant];
                float volume=ch.current_vol;
                if(ch.effect==4) volume*=progress;
                if(ch.effect==5) volume*=(1.0f-progress);
                float master=(float)(ch.is_music?vm->volume_music:vm->volume_sfx);
                float scale=ch.is_music?0.60f:1.0f;
                int level=(int)(volume*master*scale/10.0f+0.5f);
                if(level<0)level=0;
                if(level>7)level=7;
                gain[c]=(int16_t)level;
            }
        }
        int mixed=0;
        for(int c=0;c<4;++c){
            if(gain[c]==0 || delta[c]==0)continue;
            uint32_t previous=phase[c];
            uint32_t cur=previous+delta[c];
            phase[c]=cur;
            int p=(int)(cur>>24);
            int wave=(int)kind[c];
            int value;
            switch(wave){
                case 0:
                    value=p<128?((p<<1)-128):(383-(p<<1));
                    break;
                case 1:
                    value=(p<64)?(p*2-128):(127-(p-64)*4/3);
                    break;
                case 2:value=p-128;break;
                case 3:value=p<128?127:-127;break;
                case 4:value=p<64?127:-127;break;
                case 5:{
                    int tri=p<128?(p*2-128):(383-p*2);
                    int q=(p*2)&255;
                    int tri2=q<128?(q*2-128):(383-q*2);
                    value=(tri+tri2)/2;
                    break;
                }
                case 6:{
                    if(cur<previous){
                        Channel& ch=channels[c];
                        uint32_t bit=(ch.lfsr^(ch.lfsr>>1))&1u;
                        ch.lfsr=(ch.lfsr>>1)|(bit<<14);
                    }
                    value=(channels[c].lfsr&1)?127:-127;
                    break;
                }
                default:{
                    unsigned m=(p+((cur>>17)&31u))&255;
                    value=m<128?((int)m*2-128):(383-(int)m*2);
                    break;
                }
            }
            mixed+=value*(int)gain[c];
        }
        mixed*=16;
        if(mixed>32767)mixed=32767;
        if(mixed< -32768)mixed=-32768;
        out_buffer[i]=(int16_t)mixed;
    }
    return;
"""
fast=fast.replace("PHASE_TABLE",table)
s=s.replace(sig,sig+"\n#if defined(__GBA__)\n"+fast+"\n#else\n",1)
old="\n}\n\nvoid AudioEngine::update(IReal8Host *host) {"
assert s.count(old)==1
s=s.replace(old,"\n#endif\n}\n\nvoid AudioEngine::update(IReal8Host *host) {",1)
p.write_text(s)
print("Optimized GBA audio: per-sample integer mixer, 120Hz envelope/pitch updates, original 22.05kHz output.")
