#include <iostream>

using namespace std;

void test_switch_basic() {
	int score = 6;

	switch(score) {
	case 10:
	case 9:
	case 8:
		cout << "好电影" << endl;
		break;
	case 7:
	case 6:
		cout << "一般电影" << endl;
		break;
	default:
		cout << "烂电影" << endl;
		break;
	}
}

void test_switch_char() {
	char ch = 'g';
	switch(ch) {
	case 'a':
		cout << "a" << endl;
		break;
	case 'b':
		cout << "b" << endl;
		break;
	default:
		cout << "other" << endl;
		break;
	}
}

int main() {
	// test_switch_basic();
	test_switch_char();

	system("pause");

	return 0;
}