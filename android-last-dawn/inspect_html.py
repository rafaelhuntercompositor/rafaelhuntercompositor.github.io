import os,re,urllib.request,urllib.parse,json,html
base="https://html-classic.itch.zone/html/19224812/index.html?v=1789319020"
req=urllib.request.Request(base,headers={"User-Agent":"Mozilla/5.0"})
try:
 with urllib.request.urlopen(req,timeout=40) as resp:
  b=resp.read()
  print("INDEX status",resp.status,"URL",resp.url,"BYTES",len(b))
  print("HEADERS",dict(resp.headers))
  print("FIRST_TEXT",b[:3500].decode("utf-8","replace"))
  os.makedirs("downloaded",exist_ok=True)
  open("downloaded/index.html","wb").write(b)
  for l in re.findall(r'(?:src|href)\s*=\s*["\']([^"\']+)',b.decode("utf-8","replace"),flags=re.I):
   print("RESOURCE",l)
except Exception as e:
 print("ERROR",repr(e))
 raise
