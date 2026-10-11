from pathlib import Path
vm=Path("real8/src/core/real8_vm.cpp")
s=vm.read_text()
old='            host->log("!!! LUA RUNTIME ERROR !!!\\n%s", err);'
assert old in s, "runtime error handler changed"
s=s.replace(old,old+'\n            setLastError("LUA RUNTIME", "%s", err ? err : "(no details)");',1)
vm.write_text(s)
main=Path("real8/src/platforms/gba/gba_main.cpp")
s=main.read_text()
old="                vm.runFrame();"
assert old in s
s=s.replace(old,old+'\n                if (vm.lastErrorTitle[0]) { showErrorOverlay(vm, vm.lastErrorTitle, vm.lastErrorDetail, 8); }',1)
main.write_text(s)
print("Enabled visible Lua runtime error diagnostics.")
