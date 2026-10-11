from pathlib import Path
p=Path("real8/src/core/real8_vm.h")
s=p.read_text()
a="#if !defined(__GBA__) || REAL8_GBA_ENABLE_AUDIO\n  int16_t audio_buffer[4096]; // Fixed size, plenty of headroom"
b="#if !defined(__GBA__)\n  int16_t audio_buffer[4096]; // Desktop-only buffer, not referenced on GBA"
assert a in s, "VM audio buffer missing"
p.write_text(s.replace(a,b,1))
p=Path("real8/src/core/real8_audio.h")
s=p.read_text()
a="    static constexpr int SNAP_COUNT = 256;"
b="    static constexpr int SNAP_COUNT = \n#if defined(__GBA__)\n        16;\n#else\n        256;\n#endif"
assert a in s, "Snapshot count missing"
p.write_text(s.replace(a,b,1))
print("Reduced GBA unused audio buffer by 8 KiB and audio history snapshots by about 10 KiB, preserving the audio mixer.")
