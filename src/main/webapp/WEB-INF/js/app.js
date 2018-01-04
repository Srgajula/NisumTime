var myApp = angular.module(
		"myTimeApp",
		[ "ngRoute", "ngCookies", "ui.grid", "ui.grid.pagination",
				"ngMaterial", "ui.bootstrap", "pdf", 'ui.grid.selection', 'ui.grid.exporter' ]).config(
		function($mdDateLocaleProvider) {
			$mdDateLocaleProvider.formatDate = function(date) {
				var day = date.getDate();
				var month = date.getMonth() + 1;
				var year = date.getFullYear();
				return year + '-' + (month < 10 ? "0" + month : month) + '-'
						+ (day < 10 ? "0" + day : day);
			};
		});

//TODO: Replace this appUri with the domain name created
myApp.constant('appConfig', { 
			appName: "MyTime", 
			appUri: "http://localhost:8080/myTime/", 
			version:"1.0", 
			empStartId:16001, 
			empEndId:16999
		});

myApp.factory('myFactory', function() {
	var empId = "";
	var empName = "";
	var empEmailId = "";
	var empRole = "";
	var menuItems = [];
	var templateUrl = "";
	var profileUrl = "";
	var designations="";
	var technologies="";
	var shifts="";
	function setEmpId(id) {
		empId = id;
	}
	function getEmpId() {
		return empId;
	}

	function setEmpName(name) {
		empName = name;
	}
	function getEmpName() {
		return empName;
	}

	function setEmpEmailId(email) {
		empEmailId = email;
	}
	function getEmpEmailId() {
		return empEmailId;
	}

	function setEmpRole(role) {
		empRole = role;
	}

	function getEmpRole() {
		return empRole;
	}

	function setMenuItems(items) {
		menuItems = items;
	}

	function getMenuItems() {
		return menuItems;
	}
	function setDesignations(designations1) {
		designations = designations1;
	}

	function getDesignations() {
		return designations;
	}
	

	function setTechnologies(technologies1) {
		technologies = technologies1;
	}
	function getTechnologies() {
		return technologies;
	}
	function setShifts(shifts1) {
		shifts = shifts1;
	}

	function getShifts() {
		return shifts;
	}
	function setTemplateUrl(url) {
		templateUrl = url;
	}

	function getTemplateUrl() {
		return templateUrl;
	}
	
	function setProfileUrl(picurl) {
		profileUrl = picurl;
	}

	function getProfileUrl() {
		return profileUrl;
	}

	return {
		setEmpId : setEmpId,
		getEmpId : getEmpId,
		setEmpName : setEmpName,
		getEmpName : getEmpName,
		setEmpEmailId : setEmpEmailId,
		getEmpEmailId : getEmpEmailId,
		setEmpRole : setEmpRole,
		getEmpRole : getEmpRole,
		setMenuItems : setMenuItems,
		getMenuItems : getMenuItems,
		setDesignations : setDesignations,
		getDesignations : getDesignations,
		setTechnologies : setTechnologies,
		getTechnologies : getTechnologies,
		setShifts : setShifts,
		getShifts : getShifts,
		setTemplateUrl : setTemplateUrl,
		getTemplateUrl : getTemplateUrl,
		setProfileUrl : setProfileUrl,
		getProfileUrl : getProfileUrl
	}

});

myApp.factory('exportUiGridService', exportUiGridService);

    exportUiGridService.inject = ['uiGridExporterService'];
    function exportUiGridService(uiGridExporterService) {
        var service = {
            exportToExcel: exportToExcel
        };

        return service;

        function Workbook() {
            if (!(this instanceof Workbook)) return new Workbook();
            this.SheetNames = [];
            this.Sheets = {};
        }

        function exportToExcel(sheetName, gridApi, rowTypes, colTypes) {
            var columns = gridApi.grid.options.showHeader ? uiGridExporterService.getColumnHeaders(gridApi.grid, colTypes) : [];
            var data = uiGridExporterService.getData(gridApi.grid, rowTypes, colTypes);
            var fileName = gridApi.grid.options.exporterExcelFilename ? gridApi.grid.options.exporterExcelFilename : 'mytime';
            fileName += '.xlsx';
            var wb = new Workbook(),
                ws = sheetFromArrayUiGrid(data, columns);
            wb.SheetNames.push(sheetName);
            wb.Sheets[sheetName] = ws;
            var wbout = XLSX.write(wb, {
                bookType: 'xlsx',
                bookSST: true,
                type: 'binary'
            });
            saveAs(new Blob([s2ab(wbout)], {
                type: 'application/octet-stream'
            }), fileName);
        }

        function sheetFromArrayUiGrid(data, columns) {
            var ws = {};
            var range = {
                s: {
                    c: 10000000,
                    r: 10000000
                },
                e: {
                    c: 0,
                    r: 0
                }
            };
            var C = 0;
            columns.forEach(function (c) {
                var v = c.displayName || c.value || columns[i].name;
                addCell(range, v, 0, C, ws);
                C++;
            }, this);
            var R = 1;
            data.forEach(function (ds) {
                C = 0;
                ds.forEach(function (d) {
                    var v = d.value;
                    addCell(range, v, R, C, ws);
                    C++;
                });
                R++;
            }, this);
            if (range.s.c < 10000000) ws['!ref'] = XLSX.utils.encode_range(range);
            return ws;
        }
        /**
         * 
         * @param {*} data 
         * @param {*} columns 
         */

        function datenum(v, date1904) {
            if (date1904) v += 1462;
            var epoch = Date.parse(v);
            return (epoch - new Date(Date.UTC(1899, 11, 30))) / (24 * 60 * 60 * 1000);
        }

        function s2ab(s) {
            var buf = new ArrayBuffer(s.length);
            var view = new Uint8Array(buf);
            for (var i = 0; i != s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
            return buf;
        }

        function addCell(range, value, row, col, ws) {
            if (range.s.r > row) range.s.r = row;
            if (range.s.c > col) range.s.c = col;
            if (range.e.r < row) range.e.r = row;
            if (range.e.c < col) range.e.c = col;
            var cell = {
                v: value
            };
            if (cell.v == null) cell.v = '-';
            var cell_ref = XLSX.utils.encode_cell({
                c: col,
                r: row
            });

            if (typeof cell.v === 'number') cell.t = 'n';
            else if (typeof cell.v === 'boolean') cell.t = 'b';
            else if (cell.v instanceof Date) {
                cell.t = 'n';
                cell.z = XLSX.SSF._table[14];
                cell.v = datenum(cell.v);
            } else cell.t = 's';

            ws[cell_ref] = cell;
        }
    }
