from pathlib import Path
p=Path("real8/src/core/real8_vm.cpp")
s=p.read_text()
a=s.index('        if (useGbaInitWatchdog && host) host->log("[BOOT] Lua load");')
b=s.index('    #if !defined(__GBA__)\n        if (useGbaInitWatchdog && host) host->log("[BOOT] Mods");',a)
old=s[a:b]
if not ('luaL_loadbuffer(L, lua_src, lua_len' in old and 'pcallWithInitWatchdog(0)' in old):
    raise RuntimeError("Unknown interpreter source block")
new=r'''#if defined(__GBA__)
        // Compile the cart in distinct top-level function units to reduce peak parser memory.
        // Every chunk becomes an executable Lua chunk; the original game code is unchanged.
        size_t chunkStart = 0;
        unsigned chunkIndex = 0;
        while (chunkStart < lua_len) {
            size_t chunkEnd = lua_len;
            for (size_t i = chunkStart + 1; i + 9 < lua_len; ++i) {
                if (lua_src[i - 1] == '\n' && memcmp(lua_src + i, "function ", 9) == 0) {
                    chunkEnd = i;
                    break;
                }
            }
            const int status = luaL_loadbuffer(L, lua_src + chunkStart, chunkEnd - chunkStart, "cart-func");
            if (status != LUA_OK) {
                const char* err = lua_tostring(L, -1);
                setLastError("LUA PARSE", "chunk %u: %s", chunkIndex, err ? err : "(unknown)");
                lua_pop(L, 2);
                return false;
            }
            if (pcallWithInitWatchdog(0) != LUA_OK) {
                const char* err = lua_tostring(L, -1);
                setLastError("LUA RUNTIME", "chunk %u: %s", chunkIndex, err ? err : "(unknown)");
                lua_pop(L, 2);
                return false;
            }
            lua_gc(L, LUA_GCCOLLECT, 0);
            chunkStart = chunkEnd;
            ++chunkIndex;
        }
        if (host) host->log("[BOOT] Lua function chunks loaded: %u", chunkIndex);
#else
'''+old+'''#endif
'''
p.write_text(s[:a]+new+s[b:])
print("Segmented Lua parsing to free temporary parser state between function chunks.")
