; dos中断21 9号功能显示字符串
; 参考 http://spike.scu.edu.au/~barry/interrupts.html
assume cs:code

code segment
    db 'Hello world$'

    start:
        mov ax,cs
        mov ds,ax
        mov dx,0h
        mov ah,09h
        int 21h

        mov ax,4c00h
        int 21h

code ends
end start