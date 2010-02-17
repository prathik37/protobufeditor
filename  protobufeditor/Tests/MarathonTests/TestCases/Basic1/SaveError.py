useFixture(default)

def test():
	java_recorded_version = '1.6.0_17'

	if window('Protocol Buffer Editor'):
		select('FileChooser', '/C:/Program Files/RecordEdit/ProtoBuf/SampleFiles/protoStoreSales3.bin')
		click('Edit1')
		select('JTreeTable', 'cell:Tree,3(null)')
		click('New1')

		if window('Record Selection'):
			select('OptionPane.comboBox', 'Store')
			click('OK')
		close()

		select('Table', '1234', 'Data,0')
		select('Table', 'cell:Data,0(1234)')
		assert_p('Table', 'Content', '[[store, 1, , 1234, 1234], [name, 2, , , ]]')
		click('New1')

		if window('Record Selection'):
			click('OK')
		close()

		select('Table', '432', 'Data,0')
		select('Table', 'cell:Data,0(432)')
		assert_p('Table', 'Content', '[[department, 1, , 432, 432], [name, 2, , , ]]')
		click('BasicInternalFrameTitlePane$NoFocusButton2')
		select_menu('Window>>protoStoreSales3.bin>>Record: ')
		click('BasicInternalFrameTitlePane$NoFocusButton2')
		click('Save1')
		click('OptionPane.label')
		assert_p('OptionPane.label', 'Text', 'File saved, but there where records in error that did not make it on to the file')
		click('OK')
		select_menu('Window>>protoStoreSales3.bin>>Error Records')
		select('Table', 'cell:Data,0(1234)')
		assert_p('Table', 'Text', 'cell:Data,0(1234)')
		click('Right')
		select('Table', 'cell:Data,0(432)')
		assert_p('Table', 'Text', 'cell:Data,0(432)')
		click('BasicInternalFrameTitlePane$NoFocusButton2')
		select_menu('Window>>protoStoreSales3.bin>>Tree View')
		click('BasicInternalFrameTitlePane$NoFocusButton2')
	close()

