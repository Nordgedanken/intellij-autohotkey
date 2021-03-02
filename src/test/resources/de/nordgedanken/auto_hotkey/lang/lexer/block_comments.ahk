*/
/* block-comment end symbol on 1st line of a script throws an error,
but we're purposely not going to handle it for the time being since
it would increase the parse-logic complexity.
See https://www.autohotkey.com/boards/viewtopic.php?f=14&t=87455
*/

;standard
/* test1
test1
*/

;indented
	/* test2
	test2
	*/

;/* not parsed as beginning of block comment
msgbox hi /* test3
*/

;concludes only when */ is at the beginning of a line
/* test4
test4 */
*/

;random end marks should not cause errors from the parser
*/
   */

;text or comments after a block comment are fine
/* test5
test5
*/ msgbox hi ;random comment

;block comments do not have to be closed; they will automatically extend to the end of the file. (No error here)
/* test6
test6