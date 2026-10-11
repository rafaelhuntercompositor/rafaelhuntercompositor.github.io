import os,re,urllib.request,urllib.parse
BASE="https://html-classic.itch.zone/html/19224812/"
HEADERS={"User-Agent":"Mozilla/5.0"}
def get(url):
    with urllib.request.urlopen(urllib.request.Request(urllib.parse.urljoin(BASE,url),headers=HEADERS),timeout=60) as resp:
        data=resp.read()
        print("ASSET",url,"bytes",len(data),"status",resp.status)
        return data
os.makedirs("downloaded",exist_ok=True)
html=get("index.html?v=1789319020").decode("utf-8-sig","replace")
html=html.replace('<script defer src="https://static.itch.io/htmlgame.js" type="text/javascript"></script>',"")
html=html.replace("var p8_allow_mobile_menu = true;","var p8_allow_mobile_menu = false;")
html=html.replace("<head>",'<head><meta name="theme-color" content="#090b11"><style>html,body{background:#000!important;margin:0!important;padding:0!important;overflow:hidden!important}#touch_controls_gfx,#touch_controls_background,#p8_menu_buttons_touch{display:none!important}</style>',1)
open("downloaded/index.html","w",encoding="utf-8").write(html)
js=get("last_dawn.js")
open("downloaded/last_dawn.js","wb").write(js)
print("OFFLINE_BUNDLE_PREPARED bytes",len(js)+len(html.encode("utf8")))
