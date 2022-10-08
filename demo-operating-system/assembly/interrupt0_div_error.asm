; 除法错误中断处理
assume cs:code

code segment
    start:
        mov ax,0000h
        mov es,ax
        mov si,offset _interupt_zero_div_start
        mov di,0200h
        mov cl,offset _interupt_zero_div_end - offset _interupt_zero_div_start
        _copy:
            mov al,cs:[si]
            mov es:[di],al
            inc si
            inc di
            loop _copy

        ; 重新设置中断0
        mov ax,0000h
        mov es,ax
        mov word ptr es:[0000h],0000h
        mov word ptr es:[0002h],0020h

        ;int 0
        mov bl,0000h
        mov ax,1111h
        div bl

        mov ax,4c00h
        int 21h

        _interupt_zero_div_start:
            mov bx,offset _interupt_zero_div_code - offset _interupt_zero_div_start
            jmp bx

            _string_1 db 'Zero div error!$'

            _interupt_zero_div_code:
                mov bx,cs
                mov ds,bx
                mov dx,offset _string_1 - offset _interupt_zero_div_start
                mov ah,09h
                int 21h
                iret
        _interupt_zero_div_end:

code ends
end start