; 演示汇编loop指令使用
; 计算2^10
assume cs:code

code segment
    db 'ax=$'
    _varTemp dw 0000h

    start:
        mov ax,cs
        mov ds,ax

        ; 循环9次表示2^10
        mov cx,9
        mov bx,2
        mov ax,2
        s: mul bx
        loop s

        mov ds:_varTemp,ax

        ; 显示ax=字符串
        mov dx,0h
        mov ah,09h
        int 21h

        mov cl,12
        mov bx,ds:_varTemp
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h

        mov cl,8
        mov bx,ds:_varTemp
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h

        mov cl,4
        mov bx,ds:_varTemp
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h

        mov cl,0
        mov bx,ds:_varTemp
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