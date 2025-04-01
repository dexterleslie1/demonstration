; 演示多个段的程序
assume cs:code, ds:data, ss:stack

data segment
    dw 0000h
    _string_1 db 'stack=0x$'
    _string_2 db ',0x$'
data ends

stack segment
    _stack_start dw 0000h, 0000h
stack ends

code segment
    start:
        ; 初始化ds
        mov ax,data
        mov ds,ax

        ; 初始化ss、sp
        mov ax,stack
        mov ss,ax
        mov sp,0004h

        ; 设置数据为0x1234h
        mov bx,0
        mov ds:[bx],1234h

        ; 数据入栈
        mov ax,ds:[bx]
        push ax
        mov ax,ds:[bx]
        inc ax
        push ax

        ; 显示字符串
        mov dx,offset _string_1
        mov ah,09h
        int 21h

        ; 打印堆栈内容
        mov cl,12
        mov bx,ds:[_stack_start]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov cl,8
        mov bx,ds:[_stack_start]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov cl,4
        mov bx,ds:[_stack_start]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov cl,0
        mov bx,ds:[_stack_start]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov ah,02h
        mov dl,'H'
        int 21h

        ; 显示字符串
        mov dx,offset _string_2
        mov ah,09h
        int 21h

        mov cl,12
        mov bx,ds:[_stack_start+2]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov cl,8
        mov bx,ds:[_stack_start+2]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov cl,4
        mov bx,ds:[_stack_start+2]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov cl,0
        mov bx,ds:[_stack_start+2]
        shr bx,cl
        and bx,000fh
        add bl,30h
        mov ah,02h
        mov dl,bl
        int 21h
        mov ah,02h
        mov dl,'H'
        int 21h

        mov ax,4c00h
        int 21h

code ends
end start