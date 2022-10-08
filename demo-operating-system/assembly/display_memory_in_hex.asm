; 以16进制显示内存内容
assume cs:code

code segment
    dw 1234h

    start:
        mov ax,cs
        mov ds,ax
        
        mov cl,12
        mov bx,ds:[0]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h

        mov cl,8
        mov bx,ds:[0]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h

        mov cl,4
        mov bx,ds:[0]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h

        mov cl,0
        mov bx,ds:[0]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h

        mov ax,4c00h
        int 21h

code ends
end start