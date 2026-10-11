from pathlib import Path
p=Path("real8/src/platforms/gba/gba_host.cpp")
s=p.read_text()
patterns=[
 ("void IWRAM_CODE GbaHost::blitFrameDirty(","void GbaHost::blitFrameDirty("),
 ("static void IWRAM_CODE blitFrame(","static void blitFrame("),
 ("void IWRAM_CODE GbaHost::blitFrameTiles(","void GbaHost::blitFrameTiles("),
]
for a,b in patterns:
 assert a in s, f"Missing {a}"
 s=s.replace(a,b,1)
import re
s=re.sub(r"(?<![A-Za-z0-9_])IWRAM_CODE(?![A-Za-z0-9_])","",s)
p.write_text(s)
print("Moved GBA blit code from small IWRAM to ROM to allow JIT to fit.")
