; dos中断21 2号功能显示字符
; 参考 http://spike.scu.edu.au/~barry/interrupts.html
assume cs:code

code segment

    mov ah,02h
    mov dl,'p'
    int 21h

    mov ax,4c00h
    int 21h

code ends
end