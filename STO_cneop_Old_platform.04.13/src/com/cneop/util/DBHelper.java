package com.cneop.util;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "stoExpress.db";
	private static final int DATABASE_VERSION = 6;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add("create table if not exists tb_dic_user" + "(" + "	userNo varchar(15) primary key," + "	password varchar(40)," + "	userName nvarchar(40)," + "	stationId varchar(10)," + "	userType char(1)," + "	state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_station" + "(" + "		stationId varchar(10) primary key," + "		stationName nvarchar(20)," + "		attribute nvarchar(10)," + "		state char(1) default 'A'," + "		lasttime datetime default (datetime('now','localtime'))" + "	);");
		sqlList.add("create table if not exists tb_dic_signer" + "(" + "	id integer primary key AUTOINCREMENT," + "	signerName nvarchar(20)," + "	userNo varchar(15)," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_route" + "(" + "routeId varchar(10) primary key," + "routeDesc nvarchar(20)," + "nextStationName nvarchar(20)," + "nextStationId varchar(10)," + "secondStationName nvarchar(20)," + "	secondStatoinId varchar(10)," + "state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_nextStation" + "(" + "	stationId varchar(10) primary key," + "	stationName nvarchar(20)," + "	state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_destinationStation" + "(" + "	provinceNo varchar(5) primary key," + "	province nvarchar(20)," + "	alphabet varchar(20)," + "	state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_abnormal" + "(" + "	code varchar(5) primary key," + "	reasonDesc nvarchar(50)," + "	typeId varchar(20)," + "	typeName nvarchar(20)," + "	attribute nvarchar(4)," + "	state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_scopeOfDelivery" + "(" + "id integer primary key AUTOINCREMENT," + "province nvarchar(20)," + "city nvarchar(20)," + "area varchar(20)," + "	isReceipt varchar(2)," + "isDelivery varchar(2)," + "	scopeOfDelivery text," + "	scopeOfNoDelivery text," + "	state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_orderAbnormal" + "(" + "	code varchar(5) primary key," + "	reasonDesc nvarchar(50)," + "	state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_serverStation" + "(" + "	serverNo varchar(15) primary key," + "	stationName varchar(20)," + "stationCode varchar(20)," + "	serverName nvarchar(40)," + "	serverAddress varchar(10)," + "	serverPhone char(20)," + "	state char(1) default 'A'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");

		sqlList.add("create table if not exists tb_order" + "(" + "	logisticid varchar(30) primary key," + "	userNo varchar(20)," + "	acceptDate datetime," + "	customerCode varchar(20)," + "	customerName nvarchar(30)," + "	sender_Name nvarchar(20)," + "	sender_Address nvarchar(30)," + "	sender_Phone varchar(20)," + "	sender_Mobile varchar(20)," + "	destcode varchar(20)," + "cusnote varchar(50),isUrge int default 0," + "	issynchronization char(1) default '0'" + ");");

		sqlList.add("create table tb_orderOperating" + "(" + "	id integer primary key AUTOINCREMENT," + "	userNo varchar(20)," + "	employeeName nvarchar(20)," + "	employeeSite nvarchar(20)," + "	employeeSiteCode varchar(10)," + "	logisticid varchar(30)," + "	barcode varchar(20)," + "	reasonCode varchar(10)," + "	scantime datetime default (datetime('now','localtime'))," + "	flag char(1)," + "	issynchronization char(1) default '0'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_msgSend" + "(" + "	id integer primary key AUTOINCREMENT," + "	barcode varchar(20)," + "	phone varchar(20)," + "	stationId varchar(20)," + "	serverNo varchar(10)," + "	scanUser varchar(15)," + "	scanTime datetime default (datetime('now','localtime'))," + "	issynchronization char(1) default '0'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table  if not exists tb_sysConfig" + "(" + "	config_name varchar(30) primary key," + "	config_value varchar(100)," + "	config_desc varchar(20)," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table  if not exists tb_scanData" + "(" + "	id integer primary key AUTOINCREMENT," + "	barcode varchar(20)," + "	scanUser nvarchar(20)," + "	scanStation nvarchar(20)," + "	sampleType varchar(10)," + " shiftCode varchar(6)," + "	destinationSiteCode varchar(30)," + "	senderPhone varchar(20)," + "	recipientPhone varchar(10)," + "	routeCode varchar(15)," + " preStationCode varchar(15)," + "	nextStationCode varchar(10)," + "	secondStationCode varchar(10)," + "	vehicleNumber varchar(15)," + "	weight varchar(10)," + "	courier varchar(15)," + "	packageCode varchar(20)," + "	abnormalCode varchar(5)," + "	abnormalDesc nvarchar(30)," + "	carLotNumber varchar(20)," + "	flightCode varchar(20)," + "	signTypeCode varchar(10)," + "	signer nvarchar(20)," + "	siteProperties varchar(4) default '1'," + "	expressType varchar(4)," + "	scanCode varchar(4)," + "	scanTime datetime default (datetime('now','localtime'))," + "	issynchronization char(1) default '0'," + "	lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create table if not exists tb_dic_module (" + "id integer primary key AUTOINCREMENT," + "moduleName nvarchar(10)," + "parentId int, roleId int, level int," + "packageName varchar(50),imgName varchar(20)," + "initValue varchar(20),state char(1) default 'A'," + "remark varchar(20));");
		sqlList.add("create table if not exists tb_dic_scopeofdelivery (" + "Id integer primary key AUTOINCREMENT," + "province varchar(20)," + "city varchar(20)," + "area varchar(20)," + "isReceipt char(1) default '1'," + "isDelivery char(1) default '1'," + "scopeOfDelivery varchar(100)," + "scopeOfNoDelivery varchar(300)," + "state varchar(10)," + "lasttime datetime default (datetime('now','localtime'))" + ");");
		sqlList.add("create index if not exists Index_scanData_scanCode on tb_scanData(scanCode);");
		sqlList.add("create index if not exists Index_scanData_scanTime on tb_scanData(scanTime);");

		updateTo4(sqlList, db);
		try {
			for (int i = 0; i < sqlList.size(); i++) {
				db.execSQL(sqlList.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		initData(db);// 增加初始数据
	}

	private void initData(SQLiteDatabase db) {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','邮件签收章')");
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','前台签收')");
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','草签')");
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','英文')");
		// //-------------------------------------------------------------------------------
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('stationId','','网点编号')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('scanMode','single','扫描模式')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('autoUploadTimeSpilt','10','自动上传的时间间隔(分钟)')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('serviceIp','http://222.66.109.144:22230/PDATransferWS.svc','数据上传地址')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('smsInfo','http://222.66.39.194:8809/SiteInfo.ashx','服务站点下载')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('smsSend','http://222.66.39.194:8809/BqSms.ashx','短信发送')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('imageUpload','http://222.66.109.144:22221/barscanPicUpload/gunPhotoUpload.action','图片上传')");

		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('upgradeIp','http://222.66.109.144/posupgrade/','版本升级地址')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('upgradeCompanyCode','WF','升级时用的公司代码')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('companyCode','WF','公司代码')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('programRole','自动','程序角色')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('allowSelectOper','是','允许选择操作员')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('MdControlIsOpen','1','开启面单扫描管控功能')");
		// -------------------------------------------------------------------------------
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(1,'网点业务员',0,1,1,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(2,'网点扫描员',0,1,1,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(3,'中心扫描员',0,1,2,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(4,'航空扫描员',0,1,3,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(5,'航空提货员',0,1,3,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(6,'其它操作',0,1,0,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(7,'收件',1,2,1,'com.cneop.stoExpress.activity.scan.PickupChooseActivity','scan_recipient','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(8,'签收扫描',1,2,1,'com.cneop.stoExpress.activity.scan.SignChooseActivity','scan_sign','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(9,'问题件',1,2,1,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(10,'24小时件',1,2,1,'com.cneop.stoExpress.activity.MainActivity','admin_dataclear','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(11,'收件',10,3,1,'com.cneop.stoExpress.activity.scan.PickupChooseActivity','scan_recipient','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(66,'订单功能',1,2,1,'com.cneop.stoExpress.activity.scan.OrderActivity','order','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(12,'收件',2,2,1,'com.cneop.stoExpress.activity.scan.RecipientScanActivity','scan_receive','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(13,'发件',2,2,1,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(14,'到件',2,2,1,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(15,'派件',2,2,1,'com.cneop.stoExpress.activity.scan.SendingpiecesActivity','scan_pj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(16,'留仓',2,2,1,'com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity','scan_lc','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(17,'问题件',2,2,1,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(18,'装袋发件',2,2,1,'com.cneop.stoExpress.activity.scan.BaggingOutActivity','scan_zdfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(19,'车辆操作',2,2,1,'com.cneop.stoExpress.activity.MainActivity','scan_car_oper','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(20,'装车发件',19,3,1,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(21,'发车扫描',19,3,1,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_fc','fc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(22,'到车扫描',19,3,1,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_dc','dc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(23,'24小时件',2,2,1,'com.cneop.stoExpress.activity.MainActivity','admin_dataclear','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(24,'收件',23,3,1,'com.cneop.stoExpress.activity.scan.RecipientScanActivity','scan_receive','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(25,'发件',23,3,1,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(26,'到件',23,3,1,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(27,'装袋发件',23,3,1,'com.cneop.stoExpress.activity.scan.BaggingOutActivity','scan_zdfj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(28,'数据上传',6,2,0,'com.cneop.stoExpress.activity.DataUploadActivity','upload','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(29,'快件查询',6,2,0,'com.cneop.stoExpress.activity.BarcodeTrackActivity','barcode_track','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(30,'区域查询',6,2,0,'com.cneop.stoExpress.activity.AreaQueryActivity','area_query','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(32,'数据查询',6,2,0,'com.cneop.stoExpress.activity.DataQueryActivity','dataquery','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(33,'系统设置',6,2,0,'com.cneop.stoExpress.activity.SystemConfigActivity','systemconfig','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(34,'电话短信',6,2,0,'com.cneop.stoExpress.activity.PhoneMessageActivity','phone','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(64,'售后服务',6,2,0,'com.cneop.stoExpress.activity.AfterServerActivity','help','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(35,'发件',3,2,2,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(36,'到件',3,2,2,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(37,'装袋发件',3,2,2,'com.cneop.stoExpress.activity.scan.BaggingOutActivity','scan_zdfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(38,'问题件',3,2,2,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(39,'留仓',3,2,2,'com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity','scan_lc','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(40,'车辆操作',3,2,2,'com.cneop.stoExpress.activity.MainActivity','scan_car_oper','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(41,'装车发件',40,3,2,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(42,'发车扫描',40,3,2,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_fc','fc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(43,'到车扫描',40,3,2,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_dc','dc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(44,'路由发件',4,2,3,'com.cneop.stoExpress.activity.scan.OutgoingRouteActivity','scan_fj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(45,'目的地发件',4,2,3,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj_dest','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(46,'到件',4,2,3,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(47,'留仓',4,2,3,'com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity','scan_lc','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(48,'问题件',4,2,3,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(49,'包操作',4,2,3,'com.cneop.stoExpress.activity.MainActivity','scan_zdfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(50,'装包发件',49,3,3,'com.cneop.stoExpress.activity.scan.PackOutActivity','scan_zbfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(51,'发包',49,3,3,'com.cneop.stoExpress.activity.scan.FbOrDbActivity','scan_fb','fb-');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(52,'到包',49,3,3,'com.cneop.stoExpress.activity.scan.FbOrDbActivity','scan_db','db-');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(53,'车辆操作',4,2,3,'com.cneop.stoExpress.activity.MainActivity','scan_car_oper','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(54,'路由装车发件',53,3,3,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj','routezc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(55,'目的地装车发件',53,3,3,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj_dest','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(56,'发车扫描',53,3,3,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_fc','fc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(57,'到车扫描',53,3,3,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_dc','dc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(58,'24小时件',4,2,3,'com.cneop.stoExpress.activity.MainActivity','admin_dataclear','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(59,'路由发件',58,3,3,'com.cneop.stoExpress.activity.scan.OutgoingRouteActivity','scan_fj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(60,'装包发件',58,3,3,'com.cneop.stoExpress.activity.scan.PackOutActivity','scan_zbfj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(61,'发包',58,3,3,'com.cneop.stoExpress.activity.scan.FbOrDbActivity','scan_fb','fb-1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(62,'到件',58,3,3,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(63,'到包扫描',5,2,3,'com.cneop.stoExpress.activity.scan.DbScanActivity','scan_db','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(65,'服务点短信',1,2,1,'com.cneop.stoExpress.activity.scan.MsgNoticeActivity','dataquery','');");
		updateTo2(sqlList);

		db.beginTransaction();
		try {
			for (String string : sqlList) {
				db.execSQL(string);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		{
			// 数据库不升级
			if (oldVersion >= newVersion) {
				return;
			}
			List<String> sqlUpdateList = new ArrayList<String>();
			for (int i = oldVersion; i < newVersion; i++) {
				if (i == 2) {
					updateTo2(sqlUpdateList);
				}
				if (i == 3) {
					updateTo3(sqlUpdateList, db);
				}
				if (i == 4) {
					updateTo4(sqlUpdateList, db);
				}
				if (i == 5) {
					updateTo5(sqlUpdateList, db);
				}

				if (sqlUpdateList.size() == 0) {
					continue;
				}

				db.beginTransaction();
				try {
					for (String string : sqlUpdateList) {
						db.execSQL(string);
					}
					db.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("DB", "checkColumnExists1..." + e.getMessage());

				} finally {
					db.endTransaction();
				}
			}

		}

	}

	private void updateTo5(List<String> sqlUpdateList, SQLiteDatabase db) {

		sqlUpdateList.add("replace into tb_sysConfig (config_name,config_value,config_desc) values('MdControlIsOpen','1','开启面单扫描管控功能')");

	}

	private void updateTo4(List<String> sqlUpdateList, SQLiteDatabase db) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("CREATE TABLE if not exists tb_SJMDSendedRecords (  ");
		sbSql.append("extenddate nvarchar(100),  ");
		sbSql.append("sitename nvarchar(100),  ");
		sbSql.append("billcodebegin nvarchar(100),  ");
		sbSql.append("billcodeend nvarchar(100),  ");
		sbSql.append("releasequantity nvarchar(100)  ");
		sbSql.append(")  ");
		sqlUpdateList.add(sbSql.toString());
	}

	// 从第3版本开始,订单开始增加cusnote和isUrge
	private void updateTo3(List<String> sqlUpdateList, SQLiteDatabase db) {
		if (!checkColumnExist(db, "tb_order", "cusnote")) {
			sqlUpdateList.add(" alter table tb_order add cusnote varchar(50)  ");
		}
		if (!checkColumnExist(db, "tb_order", "isUrge")) {
			sqlUpdateList.add(" alter table tb_order add isUrge int  ");
		}

	}

	// 8、 屏蔽航空界面的按目的地发件、包操作——到包、装车发件——目的地装车发件
	private void updateTo2(List<String> sqlUpdateList) {
		sqlUpdateList.add(" update tb_dic_module set state='D' WHERE Id=45 ");
		sqlUpdateList.add(" update tb_dic_module set state='D' WHERE Id=52 ");
		sqlUpdateList.add(" update tb_dic_module set state='D' WHERE Id=55 ");

	}

	/**
	 * 方法1：检查某表列是否存在
	 * 
	 * @param db
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @return
	 */
	private boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			// 查询一行
			cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
			result = cursor != null && cursor.getColumnIndex(columnName) != -1;
		} catch (Exception e) {
			Log.e("DB", "checkColumnExists1..." + e.getMessage());
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return result;
	}
}
