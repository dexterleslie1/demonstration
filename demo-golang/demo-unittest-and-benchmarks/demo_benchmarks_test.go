package main

import "testing"

// Table-Driven benchmarks测试
// https://go.dev/blog/subtests
func BenchmarkAppendFloat(b *testing.B) {
	benchmarks := []struct {
		name    string
		float   float64
		fmt     byte
		prec    int
		bitSize int
	}{
		{"Decimal", 33909, 'g', -1, 64},
		{"Float", 339.7784, 'g', -1, 64},
		{"Exp", -5.09e75, 'g', -1, 64},
		{"NegExp", -5.11e-95, 'g', -1, 64},
		{"Big", 123456789123456789123456789, 'g', -1, 64},
	}
	// dst := make([]byte, 30)
	for _, bm := range benchmarks {
		b.Run(bm.name, func(b *testing.B) {
			for i := 0; i < b.N; i++ {
				// AppendFloat(dst[:0], bm.float, bm.fmt, bm.prec, bm.bitSize)
			}
		})
	}
}
