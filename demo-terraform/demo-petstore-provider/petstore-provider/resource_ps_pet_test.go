// pet资源的验收测试
package petstore

import (
	"fmt"
	"strconv"
	"testing"

	"github.com/hashicorp/terraform-plugin-sdk/v2/helper/resource"
	"github.com/hashicorp/terraform-plugin-sdk/v2/terraform"
	"github.com/petstore/petstore-api-sdk-go"
)

func TestAccPSPetBasic(t *testing.T) {
	resourceName := "petstore_pet.pet"

	resource.Test(
		t, resource.TestCase{
			PreCheck: func() {
				testAccPrecheck(t)
			},
			Providers:    testAccProviders,
			CheckDestroy: testAccCheckPSPetDestory,
			Steps: []resource.TestStep{
				{
					Config: testAccPSPetConfigBasic(),
					Check: resource.ComposeTestCheckFunc(
						resource.TestCheckResourceAttr(resourceName, "name", "am-pet"),
						resource.TestCheckResourceAttr(resourceName, "age", fmt.Sprintf("%d", 23)),
					),
				},
			},
		},
	)
}

func testAccCheckPSPetDestory(s *terraform.State) error {
	petClient := testAccProvider.Meta().(*petstore.PetClient)
	for _, rs := range s.RootModule().Resources {
		if rs.Type != "petstore_pet" {
			continue
		}

		if rs.Primary.ID == "" {
			return fmt.Errorf("No instance ID is set")
		}

		// todo
		petId, _ := strconv.ParseInt(rs.Primary.ID, 10, 64)
		_, err := petClient.Read(petId)
		if err != nil {
			return fmt.Errorf("Pet %s still exists", rs.Primary.ID)
		}
	}
	return nil
}

func testAccPSPetConfigBasic() string {
	resourceConfigStr := fmt.Sprintf(`
		resource "petstore_pet" "pet" {
			name = "am-pet"
			age = 23
		}
	`)
	return resourceConfigStr
}
