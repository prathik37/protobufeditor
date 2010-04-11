useFixture(default)

def test():
	from Modules import commonBits

	java_recorded_version = '1.6.0_17'

	if window('Protocol Buffer Editor'):
		select('FileChooser', commonBits.sampleDir() + 'protoStoreSales7.bin')
		click('Edit1')
		##select('JTreeTable', '')
		rightclick('JTreeTable', 'Tree,0')
		select_menu('Fully Expand Tree')
		select('LayoutCombo', 'Product')
		assert_p('JTreeTable', 'Content', '[[, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , 63604808, [40118], [1], [4870], [SALE], [4.87], [4.87], [\'\']], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , 69684558, [40118, 40118, 40118], [1, -1, 1], [19000, -19000, 5010], [SALE, RETURN, SALE], [19.0, -19.0, 5.01], [19.0, -19.0, 5.01], [\'\',\' -1\',\' -1 1\']], [, , 69694158, [40118, 40118, 40118], [1, -1, 1], [19000, -19000, 5010], [SALE, RETURN, SALE], [19.0, -19.0, 5.01], [19.0, -19.0, 5.01], [\'\',\' -1\',\' -1 1\']], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , 62684671, [40118, 40118], [1, -1], [69990, -69990], [SALE, RETURN], [69.99, -69.99], [69.99, -69.99], [\'\',\' -1\']], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , 65674532, [40118], [1], [3590], [SALE], [3.59], [3.59], [\'\']], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , 63674861, [40118], [10], [2700], [SALE], [2.7], [2.7], [\'\']], [, , 64634429, [40118], [1], [3990], [SALE], [3.99], [3.99], [\'\']], [, , 66624458, [40118], [1], [890], [SALE], [0.89], [0.89], [\'\']], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ], [, , , , , , , , , ]]')
		##select('JTreeTable', '')
		select('JTreeTable', 'cell:price,8([19000, -19000, 5010])')
		click('ArrowButton')
		assert_p('Table', 'Content', '[[19000], [-19000], [5010]]')
		select('Table', 'cell:A,0(19000)')
		select('Table', '19000123', 'A,0')
		select('Table', 'cell:A,1(-19000)')
		assert_p('Table', 'Content', '[[19000123], [-19000], [5010]]')
		select('Table', 'cell:A,1(-19000)')
		click('Add Row After')
		assert_p('Table', 'Content', '[[19000123], [-19000], [0], [5010]]')
		select('Table', '456', 'A,2')
		select('Table', 'cell:A,1(-19000)')
		assert_p('Table', 'Content', '[[19000123], [-19000], [456], [5010]]')
		click('BasicInternalFrameTitlePane$NoFocusButton2')
		select_menu('Window>>protoStoreSales7.bin>>Tree View')
		assert_p('JTreeTable', 'Text', '')

	close()
