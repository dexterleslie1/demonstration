//go:build windows
// +build windows

package source_code

func init() {
	Stdin = NewRawReader()
	Stdout = NewANSIWriter(Stdout)
	Stderr = NewANSIWriter(Stderr)
}
