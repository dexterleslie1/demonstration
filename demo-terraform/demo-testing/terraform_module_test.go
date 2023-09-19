package test

import (
	"bytes"
	"context"
	"fmt"
	"log"
	"net/http"
	"testing"

	"github.com/hashicorp/go-version"
	"github.com/hashicorp/hc-install/product"
	"github.com/hashicorp/hc-install/releases"
	"github.com/hashicorp/terraform-exec/tfexec"
)

// https://github.com/hashicorp/terraform-exec
func TestTerraformMoule(t *testing.T) {
	installer := &releases.ExactVersion{
		Product: product.Terraform,
		Version: version.Must(version.NewVersion("1.0.6")),
	}

	execPath, err := installer.Install(context.Background())
	if err != nil {
		log.Fatalf("error installing Terraform: %s", err)
	}

	workingDir := "./testfixtures"
	tf, err := tfexec.NewTerraform(workingDir, execPath)
	if err != nil {
		log.Fatalf("error running NewTerraform: %s", err)
	}

	err = tf.Init(context.Background(), tfexec.Upgrade(true))
	if err != nil {
		log.Fatalf("error running Init: %s", err)
	}

	defer tf.Destroy(context.Background())
	bucketName := fmt.Sprintf("my_aws_bucket_name=%s", "my-demo-bucket-dex1")
	err = tf.Apply(context.Background(), tfexec.Var(bucketName))
	if err != nil {
		log.Fatalf("error running Init: %s", err)
	}

	state, err := tf.Show(context.Background())
	if err != nil {
		log.Fatalf("error running Show: %s", err)
	}

	endpoint := state.Values.Outputs["endpoint"].Value.(string)
	url := fmt.Sprintf("http://%s", endpoint)
	resp, err := http.Get(url)
	if err != nil {
		log.Fatalf("error running http Get: %s", err)
	}

	buf := new(bytes.Buffer)
	buf.ReadFrom(resp.Body)
	log.Printf("\n%s", buf.String())

	if resp.StatusCode != http.StatusOK {
		log.Fatalf("Status code did not return 200")
	}
}
