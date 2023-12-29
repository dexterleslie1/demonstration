package demo_package_ast

import (
	"fmt"
	"go/ast"
	"go/parser"
	"go/token"
	"testing"
)

func TestDemo(t *testing.T) {
	// src is the input for which we want to inspect the AST.
	src := `
		package testdata
	
		import (
			// nothing here should be parsed
	
			// +testing:pkglvl="not here import 1"
	
			// +testing:pkglvl="not here import 2"
			foo "fmt" // +testing:pkglvl="not here import 3"
	
			// +testing:pkglvl="not here import 4"
		)
	
		// +testing:pkglvl="here unattached"
	
		// +testing:pkglvl="here reassociated"
		// +testing:typelvl="here before type"
		// +testing:eitherlvl="here not reassociated"
		// +testing:fieldlvl="not here not near field"
	
		// normal godoc
		// +testing:pkglvl="not here godoc"
		// +testing:typelvl="here on type"
		// normal godoc
		type Foo struct {
			// +testing:pkglvl="not here in struct"
			// +testing:fieldlvl="here before godoc"
	
			// normal godoc
			// +testing:fieldlvl="here in godoc"
			// +testing:pkglvl="not here godoc"
			// normal godoc
			WithGodoc string // +testing:fieldlvl="not here after field"
	
			// +testing:fieldlvl="here without godoc"
	
			WithoutGodoc int
		} // +testing:pkglvl="not here after type"
	
		// +testing:pkglvl="not here on var"
		var (
			// +testing:pkglvl="not here in var"
			Bar = "foo"
		)
	
		/* This type of doc has spaces preserved in go-ast, but we'd like to trim them. */
		type HasDocsWithSpaces struct {
		}
	
		/*
		This type of doc has spaces preserved in go-ast, but we'd like to trim them,
		especially when formatted like this.
		*/
		type HasDocsWithSpaces2 struct {
		}
	
		type Baz interface {
			// +testing:pkglvl="not here in interface"
		}
	
		// +testing:typelvl="not here beyond closest"
	
		// +testing:typelvl="here without godoc"
		// +testing:pkglvl="here reassociated no godoc"
	
		type Quux string
	
		// +testing:pkglvl="here at end after last node"
	
		/* +testing:pkglvl="not here in block" */
	
		// +testing:typelvl="here on typedecl with no more"
		type Cheese struct { }
	
		// ensure that we're fine if we've got an end-of-line
		// comment that's the last comment of the file, but
		// we still have a bit more to traverse (field list --> ident).
		// THIS MUST CONTAIN THE LAST COMMENT IN THE FILE
		// TODO(directxman12): split this off into its own case
		type private struct {
			bar int // not collected
		}
	`

	// Create the AST by parsing src.
	fset := token.NewFileSet() // positions are relative to fset
	// parser.ParseComments 表示获取代码中的注释
	f, err := parser.ParseFile(fset, "src.go", src, parser.ParseComments)
	if err != nil {
		t.Fatal(err)
	}

	/* 打印注释 */
	fmt.Println("--------------------- 打印注释 ---------------------")
	for _, comment := range f.Comments {
		fmt.Print(comment.Text())
	}

	/* visitor、walk测试 */
	fmt.Println("--------------------- visitor、walk测试 ---------------------")
	ast.Walk(myVisitor{}, f)
}

type myVisitor struct {
}

// 深度优先遍历ast每个节点
func (m myVisitor) Visit(node ast.Node) (w ast.Visitor) {
	if node != nil {
		switch n := node.(type) {
		case *ast.GenDecl:
			fmt.Printf("type %T node", node)
			fmt.Printf(", token: %s\n", n.Tok)
		// identifier
		case *ast.Ident:
			fmt.Printf("type %T node", node)
			fmt.Printf(", name: %s, object: %+v\n", n.Name, n.Obj)
		// 注释
		case *ast.Comment:
			fmt.Printf("type %T node", node)
			fmt.Printf(", text: %s\n", n.Text)
			//default:
			//	fmt.Printf("type %T node\n", node)
		}
	}
	return m
}
