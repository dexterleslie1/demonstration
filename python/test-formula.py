# 需要安装openpyxl库: pip install openpyxl
import openpyxl

# 打开Excel文件，设置data_only=True读取公式运算后的值
workbook = openpyxl.load_workbook('test-formula.xlsx', data_only=True)

# 选择活动工作表
worksheet = workbook.active

# 读取第一行第三列的值（Excel中列从A开始，第三列是C）
cell_value = worksheet.cell(row=1, column=3).value

# 打印单元格值
print(f"第一行第三列的值是: {cell_value}")

# 关闭工作簿
workbook.close()
