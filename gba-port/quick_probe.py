#!/usr/bin/env python3
import hashlib,os,re,struct,sys
from pathlib import Path
p=Path(sys.argv[1]);buf=bytearray(p.read_bytes())
slot=None
for m in re.finditer(b'P8GB',buf):
 i=m.start()
 if i+16>len(buf):continue
 flags,raw,comp=struct.unpack_from('<III',buf,i+4)
 if flags==0 and comp==raw and raw>0x4300 and i+16+raw<=len(buf) and buf[i+16+0x4300:i+16+0x4300+12]==b'-- last dawn':
  slot=i;break
if slot is None:raise RuntimeError('No Last Dawn cart blob')
size=struct.unpack_from('<I',buf,slot+8)[0]
start=slot+16
src=buf[start+0x4300:start+size].decode()
variant=os.environ.get("GBA_PROBE","simple")
if variant=="simple":
 a=src.index("function _update()")
 b=src.index("function theme()",a)
 src=src[:a]+"function _update()\n ticks=(ticks or 0)+1\n timer=(timer or 0)+1\n if timer>100 then state='menu' end\nend\n\n"+src[b:]
elif variant=="drawlight":
 a=src.index("function _draw()")
 b=src.index("function loadassets()",a)
 src=src[:a]+"function _draw()\n cls(0)\n print('ticks:'..tostr(ticks),6,6,7)\nend\n\n"+src[b:]
elif variant=="original":
 pass
else:
 raise ValueError(variant)
old="midtext('@rafaelgameaudio',108,6)"
new=old+" print('t='..tostr(timer)..' k='..tostr(ticks),6,118,7)"
if old in src: src=src.replace(old,new,1)
src=re.sub(r"(?m)^--[^\n]*\n","",src)
payload=buf[start:start+0x4300]+src.encode()
assert len(payload)<=46755, (len(payload),size)
buf[start:start+len(payload)]=payload
buf[start+len(payload):start+46755]=b'\0'*(46755-len(payload))
struct.pack_into('<II',buf,slot+8,len(payload),len(payload))
p.write_bytes(buf)
print('Patched',p,'probe',variant,'Lua bytes',len(src),'sha',hashlib.sha256(buf).hexdigest())
