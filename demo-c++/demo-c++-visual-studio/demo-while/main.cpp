#include <iostream>

using namespace std;

void test_do_while() {
	int counter = 0;
	do {
		cout << "counter=" << counter << endl;
		counter++;
	} while(counter < 10);
}

int main(){
	test_do_while();

	system("pause");
	return 0;
}