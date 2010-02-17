useFixture(default)

def test():
	from Modules import commonBits
	java_recorded_version = '1.5.0_11'

	if window('Protocol Buffer Editor'):
		select('FileChooser', commonBits.sampleDir() + 'Ams_LocDownload_20041228.bin')
		#commonBits.setRecordLayout(select, 'ams Store')
		click('Edit1')
		click('Filter')
		select('Table2', 'Loc_Type', 'Field,0')
		select('Table2', ' = ', 'Operator,0')
		select('Table2', 'dc', 'Value,0')
		select('Table2', 'Loc_Nbr', 'Field,1')
		select('Table2', '8', 'Value,1')
		select('Table2', 'cell:Value,1()')
		select('Table1', 'false', 'Include,0')
		select('Table1', 'false', 'Include,2')
		select('Table1', 'false', 'Include,7')
		select('Table1', 'false', 'Include,6')
		select('Table1', 'false', 'Include,5')
		#select('Table1', 'cell:Include,5(false)')
		click('Filter')
		select('Table', 'cell:4|Loc_Name,1(VIC West Ad Support)')
		assert_p('Table', 'Content', '[[5839, DC - Taras Ave, , VIC, A], [5850, VIC West Ad Support, , VIC, A], [5853, NSW North Sydney Ad Support, , , A], [5866, WA Ad Support, , , A], [5887, QLD Ad Support, , , A], [5888, SA Ad Support, , , A], [5895, VIC East Ad Support, , , A], [5897, Sydney Gate DC, No 2 Sydney Gate, NSW, A], [5958, State Warehouse  WA, Target State Warehouse (WA) FCL, WA, A], [5968, Beverly DC, Beverly DC, SA, A]]')
		select('Table', 'cell:4|Loc_Name,7(Sydney Gate DC)')
		assert_p('Table', 'Text', 'cell:4|Loc_Name,7(Sydney Gate DC)')
		select('Table', 'cell:4|Loc_Name,5(SA Ad Support)')
		assert_p('Table', 'RowCount', '10')
		select('Table', 'cell:4|Loc_Name,7(Sydney Gate DC)')
		rightclick('Table', '4|Loc_Name,7')
		select_menu('Edit Record')
	##	select('Table1', 'cell:4|Loc_Name,7(Sydney Gate DC)')
		select('Table', 'cell:Data,1(Sydney Gate DC)')
		assert_p('Table', 'Content', '[[Loc_Nbr, 2, , 5897, 5897], [Loc_Name, 4, , Sydney Gate DC, Sydney Gate DC], [Loc_Addr_Ln1, 5, , No 2 Sydney Gate, No 2 Sydney Gate], [Loc_State, 9, , NSW, NSW], [Loc_Actv_Ind, 10, , A, A]]')
		select('Table', 'cell:Data,3(NSW)')
		assert_p('Table', 'Text', 'cell:Data,3(NSW)')
		select('Table', 'cell:Data,1(Sydney Gate DC)')
		assert_p('Table', 'RowCount', '5')
	close()
