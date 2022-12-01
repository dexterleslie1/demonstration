#include <iostream>

using namespace std;

int main() {
	cout << "line 1" << endl;

	cout << "line 2" << endl;

	goto _l_goto;

	cout << "line 3" << endl;
	cout << "line 4" << endl;

	_l_goto:

	cout << "line 5" << endl;

	system("pause");
	return 0;
}