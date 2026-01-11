import xlwings as xw

# 1. 启动 Excel 应用程序（visible=True 表示显示 Excel 窗口，方便调试）
app = xw.App(visible=True, add_book=False)  # add_book=False 不自动创建新工作簿

# 2. 打开一个已有的 Excel 文件（如果没有则创建）
try:
    wb = app.books.open("example.xlsx")  # 打开文件
except FileNotFoundError:
    wb = app.books.add()  # 创建新工作簿
    wb.save("example.xlsx")  # 保存文件

# 3. 选择第一个工作表（Sheet1）
sheet = wb.sheets[0]  # 或 wb.sheets["Sheet1"]

# 4. 写入数据到单元格
sheet.range("A1").value = "姓名"
sheet.range("B1").value = "年龄"
sheet.range("A2").value = "张三"
sheet.range("B2").value = 25
sheet.range("A3").value = "李四"
sheet.range("B3").value = 30

# 5. 写入公式到单元格（比如计算平均年龄）
sheet.range("A4").value = "平均年龄"
sheet.range("B4").value = "=AVERAGE(B2:B3)"  # 写入公式

# 6. 触发 Excel 重新计算所有公式（确保结果最新）
wb.app.calculate()  # 或 wb.api.Application.CalculateFull()

# 7. 读取公式计算结果（此时 B4 单元格的值应该是 27.5）
average_age = sheet.range("B4").value
print(f"平均年龄计算结果：{average_age}")

# 8. 保存文件并关闭
wb.save()
wb.close()

# 9. 退出 Excel 应用程序
app.quit()