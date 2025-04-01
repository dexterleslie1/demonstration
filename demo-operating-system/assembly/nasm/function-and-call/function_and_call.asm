SECTION _v_start vstart=0x7c00
    mov ax,cs
    mov ds,ax
    mov es,ax
    mov ss,ax
    mov fs,ax
    mov sp,0x7c00
    mov ax,0xb800
    mov gs,ax
      
    ;利用int 0x10 的0x06号功能实现清屏
    mov ax,0x600
    mov bx,0x700
    mov cx,0
    mov dx,0x184f

    int 0x10
     
    ;获取当前光标位置    
    mov ah,3
    mov bh,0

    int 0x10
    ;输出字符串"HELLO MBR"
    mov ax, message
    mov bp, ax
    mov cx, 13
    mov ax, 0x1301
    mov bx, 0x2
    int 0x10
     
    jmp $   ;使CPU悬停在此

message db "HELLO MBR 888"
times 510-($-$$) db 0
db 0x55,0xaa