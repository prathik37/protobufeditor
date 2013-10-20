useFixture(default)

def test():
	from Modules import commonBits
	java_recorded_version = '1.6.0_17'

	if window('Protocol Buffer Editor'):
		select('FileChooser', commonBits.sampleDir() + 'protoStoreSales3im.bin')
		click('Edit1')
		select('LayoutCombo', 'Product')
		select('JTreeTable', 'cell:keycode,0(null)')
#		click('LineTreeChild', 1417, 191)
		click('Find1')
#		click('MetalInternalFrameTitlePane', 208, 12)
		select('TextField', '66')
		select('ComboBox', 'All Fields')
		click('Find1')
		select_menu('Window>>protoStoreSales3im.bin>>Tree View')
		select('JTreeTable', 'cell:saleDate,8(40118)')
		assert_p('JTreeTable', 'Content', '[[, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 63674861, 40118, 10, 2700], [, , 64634429, 40118, 1, 3990], [, , 66624458, 40118, 1, 890], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ]]')
		click('Find1')
#		click('BaseHelpPanel', 31, 1)
		select('ComboBox', 'All Fields')
		click('Find1')
		click('Find1')
		select_menu('Window>>protoStoreSales3im.bin>>Tree View')
		select('JTreeTable', 'cell:quantity,12(null)')
##		assert_p('JTreeTable', 'Content', '[[, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 63674861, 40118, 10, 2700], [, , 64634429, 40118, 1, 3990], [, , 66624458, 40118, 1, 890], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ]]')
		assert_p('JTreeTable', 'Content', '[[, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , 63604808, 1], [, , 69684558, 1], [, , 69694158, 1], [, , 65674532, 1], [, , 63674861, 10], [, , 64634429, 1], [, , 66624458, 1], [, , , ], [, , , ], [, , , ], [, , , ]]')
		

		select('JTreeTable', 'cell:quantity,12(null)')
		select('JTreeTable', 'cell:quantity,12(null)')
		click('Find1')
#		click('BaseHelpPanel', 21, 13)
		select('ComboBox', 'All Fields')
		click('Find1')
		click('Find1')
		click('Find1')
		click('Find1')
		click('Find1')
		select_menu('Window>>protoStoreSales3im.bin>>Tree View')
		select('JTreeTable', 'cell:keycode,37(null)')
##		assert_p('JTreeTable', 'Content', '[[, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 63674861, 40118, 10, 2700], [, , 64634429, 40118, 1, 3990], [, , 66624458, 40118, 1, 890], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 61664713, 40118, 1, 17990], [, , 61664713, 40118, -1, -17990], [, , 61684613, 40118, 1, 12990], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ]]')
		assert_p('JTreeTable', 'Content', '[[, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , 63604808, 1], [, , 69684558, 1], [, , 69694158, 1], [, , 65674532, 1], [, , 63674861, 10], [, , 64634429, 1], [, , 66624458, 1], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , 61684613, 1], [, , 68634752, 1], [, , 60664659, 1], [, , 60694698, 1], [, , 60614487, 1], [, , 63644339, 1], [, , 67674686, 1], [, , 62684217, 1], [, , 64614401, 2], [, , 64624770, 1], [, , , ], [, , , ], [, , , ]]')


		select('JTreeTable', 'cell:keycode,37(null)')
		click('Find1')
		doubleclick('Find2')
		click('Find1')
		click('Find1')
		click('Find1')
		click('Find1')

		select_menu('Window>>protoStoreSales3im.bin>>Tree View')
		select_menu('Window>>protoStoreSales3im.bin>>Tree View')
		select('JTreeTable', 'cell:keycode,57(68664211)')
##		assert_p('JTreeTable', 'Content', '[[, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 63674861, 40118, 10, 2700], [, , 64634429, 40118, 1, 3990], [, , 66624458, 40118, 1, 890], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 61664713, 40118, 1, 17990], [, , 61664713, 40118, -1, -17990], [, , 61684613, 40118, 1, 12990], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 62694485, 40118, 1, 17560], [, , 62694706, 40118, 1, 13590], [, , 62694843, 40118, 1, 13590], [, , 67644384, 40118, 1, 23960], [, , 68664211, 40118, 1, 11190], [, , 69644164, 40118, 1, 21590], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ]]')
		assert_p('JTreeTable', 'Content', '[[, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , 63604808, 1], [, , 69684558, 1], [, , 69694158, 1], [, , 65674532, 1], [, , 63674861, 10], [, , 64634429, 1], [, , 66624458, 1], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , 61684613, 1], [, , 68634752, 1], [, , 60664659, 1], [, , 60694698, 1], [, , 60614487, 1], [, , 63644339, 1], [, , 67674686, 1], [, , 62684217, 1], [, , 64614401, 2], [, , 64624770, 1], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ], [, , , ]]')

##		assert_p('JTreeTable', 'Content', '[[, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 63674861, 40118, 10, 2700], [, , 64634429, 40118, 1, 3990], [, , 66624458, 40118, 1, 890], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , 61664713, 40118, 1, 17990], [, , 61664713, 40118, -1, -17990], [, , 61684613, 40118, 1, 12990], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ], [, , , , , ]]')

		
	close()
