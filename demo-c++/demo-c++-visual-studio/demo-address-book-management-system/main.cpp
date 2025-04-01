#include <iostream>
#include <string>
#include <assert.h>

using namespace std;

#define Max 1000

struct person {
	string name;
	int sex;
};

struct address_book {
	struct person person_arr[Max];
	int size;
};

// 显式菜单
void show_menu() {
	cout << "*************************" << endl;
	cout << "***** 1、添加联系人 *****" << endl;
	cout << "***** 2、显示联系人 *****" << endl;
	cout << "***** 3、删除联系人 *****" << endl;
	cout << "***** 0、退出通讯录 *****" << endl;
	cout << "*************************" << endl;
	cout << "请输入菜单编号进入功能：";
}

void add_person(address_book *ptrAddressBook) {
	assert(ptrAddressBook != NULL);

	if(ptrAddressBook->size >= Max) {
		cout << "人数已满不能继续添加" << endl;
		return;
	} else {
		string name;
		int sex = 0;

		cout << "请输入名字:";
		cin >> name;

		while(sex != 1 && sex != 2) {
			cout << "请输入性别（1、男 2、女）：";
			cin >> sex;
		}
		
		int size = ptrAddressBook->size;
		ptrAddressBook->person_arr[size].name = name;
		ptrAddressBook->person_arr[size].sex = sex;
		ptrAddressBook->size = size + 1;
		
		cout << "添加联系人成功" << endl;
	}
}

void show_person(address_book *ptrAddressBook) {
	int size = ptrAddressBook->size;
	if(size == 0) {
		cout << "当前没有联系人" << endl;
	} else {
		for(int i=0; i<size; i++) {
			struct person person_v = ptrAddressBook->person_arr[i];
			cout << "姓名:" << person_v.name << " 性别:" << (person_v.sex==1?"男":"女") << endl;
		}
	}
}

void delete_person(address_book *ptrAddressBook) {
	int size = ptrAddressBook->size;
	if(size == 0) {
		cout << "当前没有联系人" << endl;
	} else {
		string name;
		cout << "输入名称：";
		cin >> name;
		int found = 0;
		for(int i=0; i<size; i++) {
			string nameTemp = ptrAddressBook->person_arr[i].name;
			if(nameTemp.compare(name) == 0) {
				found = 1;

				// 联系人数组记录前移做到删除的效果
				if(i != (size - 1)) {
					for(int j=i; j<size-1; j++) {
						ptrAddressBook->person_arr[j] = ptrAddressBook->person_arr[j+1];
					}

					ptrAddressBook->size = size - 1;
					break;
				} else {
					ptrAddressBook->size = size -1;
				}

				cout << "删除成功" << endl;
			}
		}

		if(!found) {
			cout << "没有关于名称为 " << name << " 的联系人" << endl;
		}
	}
}

int main() {
	struct address_book address_book_v;
	address_book_v.size = 0;

	int select = 0;
	while(true) {
		show_menu();

		cin >> select;

		switch(select) {
		case 0:
			cout << "欢迎下次使用" << endl;
			system("pause");
			return 0;
			break;
		case 1:
			add_person(&address_book_v);
			break;
		case 2:
			show_person(&address_book_v);
			break;
		case 3:
			delete_person(&address_book_v);
			break;
		}

		system("pause");
		system("cls");
	}
}