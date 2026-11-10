from PIL import Image, ImageFilter, ImageDraw
import os
SRC = "logo-source.webp"; OUT = "out"
os.makedirs(OUT, exist_ok=True)
im = Image.open(SRC).convert("RGB"); W,H = im.size
bg=(227,207,179); cc=(100,72,52); px=im.load()
def dist(a,b): return ((a[0]-b[0])**2+(a[1]-b[1])**2+(a[2]-b[2])**2)**0.5
m=Image.new("L",(W,H),0); p=m.load()
for y in range(H):
    for x in range(W):
        if dist(px[x,y],cc)<110: p[x,y]=255
m=m.filter(ImageFilter.MaxFilter(3)).filter(ImageFilter.GaussianBlur(2.0)).point(lambda v:255 if v>127 else 0)
f=m.copy(); ImageDraw.floodfill(f,(512,450),128,thresh=0)
reg=Image.new("L",(W,H),0); rp=reg.load(); fp=f.load()
for y in range(H):
    for x in range(W):
        if fp[x,y]==128: rp[x,y]=255
bb=reg.getbbox(); print("bbox",bb)
reg=reg.crop(bb).filter(ImageFilter.GaussianBlur(0.7))
solid=Image.new("RGBA",reg.size,cc+(0,)); solid.putalpha(reg)
pad=6; logo=Image.new("RGBA",(solid.width+pad*2,solid.height+pad*2),cc+(0,)); logo.alpha_composite(solid,(pad,pad))
logo.save(f"{OUT}/logo_mark.png")
print("logo",logo.size)
def on_bg(size,occ):
    art=Image.new("RGBA",(size,size),bg+(255,)); target=int(size*occ); sc=target/max(logo.size)
    lw,lh=int(logo.width*sc),int(logo.height*sc); r=logo.resize((lw,lh),Image.LANCZOS)
    art.alpha_composite(r,((size-lw)//2,(size-lh)//2)); return art.convert("RGB")
on_bg(512,0.62).save(f"{OUT}/icon_512.png")
fg_size=432; fg=Image.new("RGBA",(fg_size,fg_size),(0,0,0,0)); target=int(fg_size*0.56); sc=target/max(logo.size)
lw,lh=int(logo.width*sc),int(logo.height*sc); fg.alpha_composite(logo.resize((lw,lh),Image.LANCZOS),((fg_size-lw)//2,(fg_size-lh)//2))
fg.save(f"{OUT}/ic_launcher_foreground.png")
mono=Image.new("RGBA",fg.size,(0,0,0,0)); mono.alpha_composite(fg); mp=mono.load()
for y in range(fg_size):
    for x in range(fg_size):
        r,g,b,a=mp[x,y]; mp[x,y]=(255,255,255,a)
mono.save(f"{OUT}/ic_launcher_monochrome.png")
for name,size in {"mdpi":48,"hdpi":72,"xhdpi":96,"xxhdpi":144,"xxxhdpi":192}.items():
    on_bg(size,0.60).save(f"{OUT}/ic_launcher_{name}.png")
print("done")


