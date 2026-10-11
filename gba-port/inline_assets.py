from pathlib import Path
import hashlib,re,struct,sys

def patch_gba(path):
    p=Path(path)
    rom=bytearray(p.read_bytes())
    offset=None
    for m in re.finditer(re.escape(b'P8GB'),rom):
        i=m.start()
        if i+16>len(rom): continue
        flags,raw,comp=struct.unpack_from('<III',rom,i+4)
        if flags==0 and raw==comp and raw>0x4300 and i+16+comp<=len(rom) and rom[i+16+0x4300:i+16+0x4300+12]==b'-- last dawn':
            offset=i; break
    if offset is None: raise RuntimeError("Last Dawn cart blob was not found in GBA ROM.")
    _,old_size,_=struct.unpack_from('<III',rom,offset+4)
    start=offset+16
    data=bytearray(rom[start:start+old_size])
    fixed_size=0x4300
    source=data[fixed_size:].decode("utf-8")
    marker="function loadassets()"
    assert source.count(marker)==1
    cut=source.index(marker)
    embedded=source[cut:]
    assert embedded.endswith("\nend\n")
    assets=re.findall(r'local data="([0-9a-f]+)"\n for i=1,#data,2 do poke\((\d+)\+?\(i-1\)/2,tonum\("0x"\.\.sub\(data,i,i\+1\)\)\) end',embedded)
    assert len(assets)==2, "Unexpected asset format"
    assert sorted((int(base),len(encoded)//2) for encoded,base in assets)==[(0,8192),(12544,4608)],"Unexpected asset offsets"
    for encoded,base in assets:
        binary=bytes.fromhex(encoded)
        mem=int(base)
        data[mem:mem+len(binary)]=binary
        print(f"Embedded {len(binary)} original asset bytes into PICO-8 memory at 0x{mem:x}")
    stripped=source[:cut]+"function loadassets() end\n"
    if __import__("os").environ.get("HUNTER_GBA_DIAGNOSTIC")=="1":
        old="midtext('@rafaelgameaudio',108,6)"
        assert old in stripped
        stripped=stripped.replace(old,old+" print('t='..tostr(timer)..' f='..tostr(ticks),10,118,7)",1)
        print("Diagnostic Lua splash timer overlay injected.")
    payload=data[:fixed_size]+stripped.encode("utf-8")
    assert len(payload)<old_size
    rom[start:start+len(payload)]=payload
    rom[start+len(payload):start+old_size]=b"\0"*(old_size-len(payload))
    struct.pack_into("<II",rom,offset+8,len(payload),len(payload))
    p.write_bytes(rom)
    print("Original code:",len(source),"optimized code:",len(stripped),"ROM bytes:",len(rom))
    print("ROM SHA256:",hashlib.sha256(rom).hexdigest())

if __name__=="__main__":patch_gba(sys.argv[1])
