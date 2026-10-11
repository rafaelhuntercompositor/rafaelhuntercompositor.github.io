from pathlib import Path
p=Path("real8/src/platforms/gba/gba_main.cpp")
s=p.read_text()
a="        REAL8_PROFILE_FRAME_BEGIN(&vm);"
assert a in s
s=s.replace(a,'''        static unsigned probeFrames=0;
        ++probeFrames;
        BG_PALETTE[30] = RGB5(31, 0, 0);
        BG_PALETTE[31] = RGB5(0, 31, 0);
        volatile u16* bar=(volatile u16*)VRAM;
        unsigned pos=(probeFrames/4u)%112u;
        unsigned pixel=156u*120u+pos;
        bar[pixel]=0x1e1e;
''' + a,1)
b="        vm.show_frame();\n        REAL8_PROFILE_END(&vm, Real8VM::kProfileBlit);"
assert b in s
s=s.replace(b,'''        vm.show_frame();
        bar[156u*120u + ((probeFrames/4u)%112u)] = 0x1f1f;
        REAL8_PROFILE_END(&vm, Real8VM::kProfileBlit);''',1)
p.write_text(s)
print("Added GBA main loop heartbeat at bottom border; red before VM, green after frame blit.")
