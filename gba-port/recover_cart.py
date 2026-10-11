#!/usr/bin/env python3
import hashlib, re, struct, sys, urllib.request, zlib
from pathlib import Path

URL = "https://html-classic.itch.zone/html/19224812/last_dawn.js"
EXPECTED = "f4cd147e2a0320f2ed610f7b820070c00caf7300c52c9f0f67c7b7d055e7bd4c"

def chunk(name, data):
    return struct.pack(">I",len(data))+name+data+struct.pack(">I", zlib.crc32(name+data)&0xffffffff)

def png_from_pico8_rom(cart):
    width, height = 160,205
    data=bytearray()
    for y in range(height):
        data.append(0)
        for x in range(width):
            i=y*width+x
            value=cart[i] if i<len(cart) else 0
            # Encode one cart byte per RGBA pixel, two low bits from each channel.
            data.extend((16|((value>>4)&3),12|((value>>2)&3),20|(value&3),252|((value>>6)&3)))
    return (b"\x89PNG\r\n\x1a\n"+
            chunk(b"IHDR",struct.pack(">IIBBBBB",width,height,8,6,0,0,0))+
            chunk(b"IDAT",zlib.compress(bytes(data),9))+chunk(b"IEND",b""))

def main():
    req=urllib.request.Request(URL,headers={"User-Agent":"Mozilla/5.0"})
    with urllib.request.urlopen(req,timeout=75) as response:
        js=response.read().decode("latin1")
    matched=re.search(r"var\s+_cartdat\s*=\s*\[([^]]*)\]",js,re.S)
    if not matched:
        raise RuntimeError("O arquivo público do itch.io não contém _cartdat.")
    cart=bytes(int(d) for d in re.findall(r"\d+",matched.group(1)))
    if len(cart)!=32768:
        raise RuntimeError(f"Cartucho incompleto: {len(cart)} bytes.")
    digest=hashlib.sha256(cart).hexdigest()
    if digest != EXPECTED:
        raise RuntimeError(f"SHA256 inesperado: {digest}")
    out=Path(sys.argv[1] if len(sys.argv)>1 else "game.p8.png")
    out.parent.mkdir(parents=True,exist_ok=True)
    out.write_bytes(png_from_pico8_rom(cart))
    print(f"Cartucho PICO-8 recuperado: {len(cart)} bytes; sha256: {digest}")
    print(f"PNG criado em: {out} ({out.stat().st_size} bytes).")

if __name__=="__main__":
    main()
