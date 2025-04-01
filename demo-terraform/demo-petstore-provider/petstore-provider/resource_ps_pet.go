// 定义了管理pet资源的crud操作资源
package petstore

import (
	"fmt"
	"strconv"

	"github.com/hashicorp/terraform-plugin-sdk/v2/helper/schema"
	"github.com/petstore/petstore-api-sdk-go"
)

func resourcePSPet() *schema.Resource {
	return &schema.Resource{
		Create: resourcePSPetCreate,
		Read:   resourcePSPetRead,
		Update: resourcePSPetUpdate,
		Delete: resourcePSPetDelete,
		Importer: &schema.ResourceImporter{
			State: schema.ImportStatePassthrough,
		},

		Schema: map[string]*schema.Schema{
			"name": {
				Type:     schema.TypeString,
				Optional: true,
				Default:  "",
			},
			"age": {
				Type:     schema.TypeInt,
				Required: true,
			},
		},
	}
}

func resourcePSPetCreate(d *schema.ResourceData, meta interface{}) error {
	petClient := meta.(*petstore.PetClient)
	options := petstore.PetCreateOptions{
		Name: d.Get("name").(string),
		Age:  d.Get("age").(int),
	}
	pet, err := petClient.Create(options)
	if err != nil {
		return err
	}
	d.SetId(fmt.Sprintf("%d", pet.Id))
	return resourcePSPetRead(d, meta)
}

func resourcePSPetRead(d *schema.ResourceData, meta interface{}) error {
	petClient := meta.(*petstore.PetClient)
	// todo
	petId, _ := strconv.ParseInt(d.Id(), 10, 64)
	pet, err := petClient.Read(petId)
	if err != nil {
		return err
	}

	d.Set("name", pet.Name)
	d.Set("age", pet.Age)
	return nil
}

func resourcePSPetDelete(d *schema.ResourceData, meta interface{}) error {
	petClient := meta.(*petstore.PetClient)

	petId, _ := strconv.ParseInt(d.Id(), 10, 64)
	err := petClient.Delete(petId)
	if err != nil {
		return err
	}

	return nil
}

func resourcePSPetUpdate(d *schema.ResourceData, meta interface{}) error {
	petClient := meta.(*petstore.PetClient)
	options := petstore.PetUpdateOptions{}

	if d.HasChange("name") {
		options.Name = d.Get("name").(string)
	}
	if d.HasChange("age") {
		options.Age = d.Get("Age").(int)
	}

	petId, _ := strconv.ParseInt(d.Id(), 10, 64)
	_, err := petClient.Update(petId, options)
	if err != nil {
		return err
	}

	return resourcePSPetRead(d, meta)
}
