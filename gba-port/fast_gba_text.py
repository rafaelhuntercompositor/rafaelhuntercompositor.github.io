from pathlib import Path
p=Path("real8/src/core/real8_gfx.cpp")
s=p.read_text()
needle="int Real8Gfx::pprint(const char *s, int len, int x, int y, uint8_t col) {\n    invalidateObjBatch();"
assert needle in s
fast=r"""
#if defined(__GBA__)
    // Glyphs go directly to 8-bit framebuffer for the frequent simple PICO-8
    // text drawing case. Preserve original path for fill patterns, alternate
    // fonts, masking, or other advanced API combinations.
    if (vm && vm->draw_fb() && !use_alt_font &&
        draw_mask == 0 && fillp_pattern == 0xFFFFFFFFu &&
        !std::memchr(s,14,(size_t)len) && !std::memchr(s,15,(size_t)len)) {
        int cx=x,cy=y, minx=128,miny=128,maxx=-1,maxy=-1;
        const int adv=use_menu_font?5:4;
        const uint8_t mapped=palette_map[col&15];
        const int loX=clip_x,loY=clip_y;
        const int hiX=clip_x+clip_w,hiY=clip_y+clip_h;
        const int screenW=vm->draw_w(),screenH=vm->draw_h();
        for(int i=0;i<len;i++){
            unsigned char ch=(unsigned char)s[i];
            if(ch=='\n'){cy+=6;cx=x;continue;}
            if(ch=='\r'){cx=x;continue;}
            if(ch=='\t'){cx=((cx-x+16)/16)*16+x;continue;}
            if(ch=='\b'){cx=cx-5<x?x:cx-5;continue;}
            if(ch<16) continue;
            const uint8_t* glyph=use_menu_font?p8_5x6_bits(ch):p8_4x6_bits(ch);
            int gx=cx-cam_x;
            int gy=cy-cam_y;
            if(gx<screenW && gx+3>=0 && gy<screenH && gy+5>=0){
                for(int row=0;row<6;row++){
                    int sy=gy+row;
                    if(sy<loY || sy>=hiY || sy<0 || sy>=screenH)continue;
                    uint8_t* dst=vm->draw_fb_row(sy);
                    uint8_t bits=glyph[row];
                    for(int colx=0;colx<4;colx++){
                        int sx=gx+colx;
                        if(sx<loX || sx>=hiX || sx<0 || sx>=screenW)continue;
                        if(bits&(0x80u>>colx)){
                            dst[sx]=mapped;
                            if(sx<minx)minx=sx;
                            if(sy<miny)miny=sy;
                            if(sx>maxx)maxx=sx;
                            if(sy>maxy)maxy=sy;
                        }
                    }
                }
            }
            cx+=adv;
        }
        if(maxx>=0 && maxy>=0)vm->mark_draw_dirty_rect(minx,miny,maxx,maxy);
        return cx;
    }
#endif
"""
s=s.replace(needle,needle+"\n"+fast,1)
p.write_text(s)
print("PICO-8 glyphs now use GBA direct framebuffer fast path, preserving complex font semantics.")
