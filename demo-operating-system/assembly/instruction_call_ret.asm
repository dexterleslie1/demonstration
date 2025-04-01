; 演示call、ret指令使用
assume cs:code, ds:data, ss:stack

data segment
    _string_1 db 'Hello world!$'
    _string_2 db 'Hello world2!$'
data ends

stack segment
    _stack_start dw 0000h, 0000h, 0000h, 0000h
stack ends

code segment
    start:
        ; 初始化ds
        mov ax,data
        mov ds,ax

        ; 初始化ss、sp
        mov ax,stack
        mov ss,ax
        mov sp,0008h

        call _display_string

        mov dx,offset _string_2
        mov ah,09h
        int 21h

        mov ax,4c00h
        int 21h

        _display_string:
            mov dx,offset _string_1
            mov ah,09h
            int 21h
            ret

code ends
end start