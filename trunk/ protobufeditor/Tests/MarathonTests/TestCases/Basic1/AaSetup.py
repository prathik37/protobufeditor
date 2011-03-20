useFixture(default)

def test():
	java_recorded_version = '1.6.0_17'

	if window('Protocol Buffer Editor'):
		click('Preferences')

		if window('Record Editor Options Editor'):
			select('TabbedPane', 'Properties')
			select('TabbedPane1', 'Other Options')
			select('EditPropertiesPnl$BoolFld', 'true')
			select('EditPropertiesPnl$BoolFld1', 'false')
			select('EditPropertiesPnl$BoolFld5', 'false')
			##select('TabbedPane1', 'Big Model Options')
			##select('EditPropertiesPnl$BoolFld13', 'false')
			select('TabbedPane', 'Looks')
			select('ComboBox1', 'Default')
			click('Save')
		close()
	close()
