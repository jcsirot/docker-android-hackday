// Copyright 2015 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

// Package cli is a trivial package for gomobile bind example.
package cli

import "fmt"

const (
	ver = "Client:\r\n"+
 "Version:	17.12.0-ce-rc2\r\n"+
 "API version:	1.35\r\n"+
 "Go version:	go1.9.2\r\n"+
 "Git commit:	f9cde63\r\n"+
 "Built:	Tue Dec 12 06:39:10 2017\r\n"+
 "OS/Arch:	darwin/amd64\r\n"
)

func Version() string {
	return fmt.Sprintf("%s", ver)
}
