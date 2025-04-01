package petstore_test

import (
	"testing"

	"github.com/petstore/petstore-api-sdk-go"
)

func TestAll(t *testing.T) {
	host := "localhost"
	port := 8080
	petClient, err := petstore.NewPetClient(host, port)
	if err != nil {
		t.Fatalf("%s", err)
	}

	petName := "pet1"
	petAge := 23
	pet, err := petClient.Create(petstore.PetCreateOptions{
		Name: petName,
		Age:  petAge,
	})
	if err != nil {
		t.Fatalf("调用petClient.Create失败，原因: %s", err)
	}

	tempPet, err := petClient.Read(pet.Id)
	if petName != tempPet.Name {
		t.Fatalf("pet名称不是预期值")
	}
	if petAge != tempPet.Age {
		t.Fatalf("pet年龄不是预期值")
	}
	if pet.Name != tempPet.Name {
		t.Fatalf("pet名称不是预期值")
	}
	if pet.Age != tempPet.Age {
		t.Fatalf("pet年龄不是预期值")
	}

	// 修改pet
	petNewName := "netpet1"
	petNewAge := 25
	pet, err = petClient.Update(pet.Id, petstore.PetUpdateOptions{
		Name: petNewName,
		Age:  petNewAge,
	})
	if err != nil {
		t.Fatalf("调用petClient.Update失败，原因: %s", err)
	}
	if petNewName != pet.Name {
		t.Fatalf("没有成功修改pet名称")
	}
	if petNewAge != pet.Age {
		t.Fatalf("没有成功修改pet年龄")
	}

	err = petClient.Delete(pet.Id)
	if err != nil {
		t.Fatalf("调用petClient.Delete失败，原因: %s", err)
	}

	pet, err = petClient.Read(pet.Id)
	if err != nil {
		t.Fatalf("调用petClient.Read失败，原因: %s", err)
	}
	if pet != nil {
		t.Fatal("pet预期为nil，但当前不为nil")
	}
}
