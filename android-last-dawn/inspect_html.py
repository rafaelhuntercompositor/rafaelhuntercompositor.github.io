import os,re,urllib.request,urllib.parse
base="https://html-classic.itch.zone/html/19224812/"
headers={"User-Agent":"Mozilla/5.0"}
def fetch(path):
 u=urllib.parse.urljoin(base,path)
 with urllib.request.urlopen(urllib.request.Request(u,headers=headers),timeout=50) as resp:
  b=resp.read()
  print("FETCH",u,"STATUS",resp.status,"SIZE",len(b),"TYPE",resp.headers.get("Content-Type"))
  return b
os.makedirs("downloaded",exist_ok=True)
page=fetch("index.html?v=1789319020")
text=page.decode("utf-8-sig","replace")
text=text.replace('<script defer src="https://static.itch.io/htmlgame.js" type="text/javascript"></script>','')
text=text.replace('var p8_allow_mobile_menu = true;','var p8_allow_mobile_menu = false;')
open("downloaded/index.html","w",encoding="utf-8").write(text)
for path in ["last_dawn.js"]:
 try:
  b=fetch(path)
  open("downloaded/"+path,"wb").write(b)
  s=b.decode("latin1","replace")
  print("JS_START",s[:350].replace("\n"," "))
  print("JS_END",s[-350:].replace("\n"," "))
  for pat in ["wasm","\.data","fetch(","locateFile","pico8","last_dawn","base64"]:
   print("PAT",pat,len(re.findall(pat,s,re.I)))
 except Exception as e: print("ASSET_ERROR",path,repr(e));raise
print("RESOURCES",re.findall(r'(?:src|href)\s*=\s*["\']([^"\']+)',text,flags=re.I)[:20])
