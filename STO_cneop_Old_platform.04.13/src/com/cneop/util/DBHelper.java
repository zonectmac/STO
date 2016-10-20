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
		initData(db);// ���ӳ�ʼ����
	}

	private void initData(SQLiteDatabase db) {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','�ʼ�ǩ����')");
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','ǰ̨ǩ��')");
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','��ǩ')");
		sqlList.add("insert into tb_dic_signer (userNo,signerName) values('system','Ӣ��')");
		// //-------------------------------------------------------------------------------
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('stationId','','������')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('scanMode','single','ɨ��ģʽ')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('autoUploadTimeSpilt','10','�Զ��ϴ���ʱ����(����)')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('serviceIp','http://222.66.109.144:22230/PDATransferWS.svc','�����ϴ���ַ')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('smsInfo','http://222.66.39.194:8809/SiteInfo.ashx','����վ������')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('smsSend','http://222.66.39.194:8809/BqSms.ashx','���ŷ���')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('imageUpload','http://222.66.109.144:22221/barscanPicUpload/gunPhotoUpload.action','ͼƬ�ϴ�')");

		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('upgradeIp','http://222.66.109.144/posupgrade/','�汾������ַ')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('upgradeCompanyCode','WF','����ʱ�õĹ�˾����')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('companyCode','WF','��˾����')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('programRole','�Զ�','�����ɫ')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('allowSelectOper','��','����ѡ�����Ա')");
		sqlList.add("insert into tb_sysConfig (config_name,config_value,config_desc) values('MdControlIsOpen','1','�����浥ɨ��ܿع���')");
		// -------------------------------------------------------------------------------
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(1,'����ҵ��Ա',0,1,1,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(2,'����ɨ��Ա',0,1,1,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(3,'����ɨ��Ա',0,1,2,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(4,'����ɨ��Ա',0,1,3,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(5,'�������Ա',0,1,3,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(6,'��������',0,1,0,'','','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(7,'�ռ�',1,2,1,'com.cneop.stoExpress.activity.scan.PickupChooseActivity','scan_recipient','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(8,'ǩ��ɨ��',1,2,1,'com.cneop.stoExpress.activity.scan.SignChooseActivity','scan_sign','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(9,'�����',1,2,1,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(10,'24Сʱ��',1,2,1,'com.cneop.stoExpress.activity.MainActivity','admin_dataclear','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(11,'�ռ�',10,3,1,'com.cneop.stoExpress.activity.scan.PickupChooseActivity','scan_recipient','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(66,'��������',1,2,1,'com.cneop.stoExpress.activity.scan.OrderActivity','order','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(12,'�ռ�',2,2,1,'com.cneop.stoExpress.activity.scan.RecipientScanActivity','scan_receive','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(13,'����',2,2,1,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(14,'����',2,2,1,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(15,'�ɼ�',2,2,1,'com.cneop.stoExpress.activity.scan.SendingpiecesActivity','scan_pj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(16,'����',2,2,1,'com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity','scan_lc','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(17,'�����',2,2,1,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(18,'װ������',2,2,1,'com.cneop.stoExpress.activity.scan.BaggingOutActivity','scan_zdfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(19,'��������',2,2,1,'com.cneop.stoExpress.activity.MainActivity','scan_car_oper','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(20,'װ������',19,3,1,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(21,'����ɨ��',19,3,1,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_fc','fc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(22,'����ɨ��',19,3,1,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_dc','dc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(23,'24Сʱ��',2,2,1,'com.cneop.stoExpress.activity.MainActivity','admin_dataclear','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(24,'�ռ�',23,3,1,'com.cneop.stoExpress.activity.scan.RecipientScanActivity','scan_receive','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(25,'����',23,3,1,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(26,'����',23,3,1,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(27,'װ������',23,3,1,'com.cneop.stoExpress.activity.scan.BaggingOutActivity','scan_zdfj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(28,'�����ϴ�',6,2,0,'com.cneop.stoExpress.activity.DataUploadActivity','upload','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(29,'�����ѯ',6,2,0,'com.cneop.stoExpress.activity.BarcodeTrackActivity','barcode_track','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(30,'�����ѯ',6,2,0,'com.cneop.stoExpress.activity.AreaQueryActivity','area_query','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(32,'���ݲ�ѯ',6,2,0,'com.cneop.stoExpress.activity.DataQueryActivity','dataquery','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(33,'ϵͳ����',6,2,0,'com.cneop.stoExpress.activity.SystemConfigActivity','systemconfig','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(34,'�绰����',6,2,0,'com.cneop.stoExpress.activity.PhoneMessageActivity','phone','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(64,'�ۺ����',6,2,0,'com.cneop.stoExpress.activity.AfterServerActivity','help','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(35,'����',3,2,2,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(36,'����',3,2,2,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(37,'װ������',3,2,2,'com.cneop.stoExpress.activity.scan.BaggingOutActivity','scan_zdfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(38,'�����',3,2,2,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(39,'����',3,2,2,'com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity','scan_lc','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(40,'��������',3,2,2,'com.cneop.stoExpress.activity.MainActivity','scan_car_oper','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(41,'װ������',40,3,2,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(42,'����ɨ��',40,3,2,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_fc','fc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(43,'����ɨ��',40,3,2,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_dc','dc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(44,'·�ɷ���',4,2,3,'com.cneop.stoExpress.activity.scan.OutgoingRouteActivity','scan_fj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(45,'Ŀ�ĵط���',4,2,3,'com.cneop.stoExpress.activity.scan.OutgoingStationActivity','scan_fj_dest','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(46,'����',4,2,3,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(47,'����',4,2,3,'com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity','scan_lc','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(48,'�����',4,2,3,'com.cneop.stoExpress.activity.scan.ExceptionActivity','scan_problem','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(49,'������',4,2,3,'com.cneop.stoExpress.activity.MainActivity','scan_zdfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(50,'װ������',49,3,3,'com.cneop.stoExpress.activity.scan.PackOutActivity','scan_zbfj','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(51,'����',49,3,3,'com.cneop.stoExpress.activity.scan.FbOrDbActivity','scan_fb','fb-');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(52,'����',49,3,3,'com.cneop.stoExpress.activity.scan.FbOrDbActivity','scan_db','db-');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(53,'��������',4,2,3,'com.cneop.stoExpress.activity.MainActivity','scan_car_oper','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(54,'·��װ������',53,3,3,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj','routezc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(55,'Ŀ�ĵ�װ������',53,3,3,'com.cneop.stoExpress.activity.scan.ZCFJActivity','scan_zcfj_dest','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(56,'����ɨ��',53,3,3,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_fc','fc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(57,'����ɨ��',53,3,3,'com.cneop.stoExpress.activity.scan.FcOrDcActivity','scan_dc','dc');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(58,'24Сʱ��',4,2,3,'com.cneop.stoExpress.activity.MainActivity','admin_dataclear','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(59,'·�ɷ���',58,3,3,'com.cneop.stoExpress.activity.scan.OutgoingRouteActivity','scan_fj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(60,'װ������',58,3,3,'com.cneop.stoExpress.activity.scan.PackOutActivity','scan_zbfj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(61,'����',58,3,3,'com.cneop.stoExpress.activity.scan.FbOrDbActivity','scan_fb','fb-1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(62,'����',58,3,3,'com.cneop.stoExpress.activity.scan.TopiecesActivity','scan_dj','1');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(63,'����ɨ��',5,2,3,'com.cneop.stoExpress.activity.scan.DbScanActivity','scan_db','');");
		sqlList.add("insert into tb_dic_module(Id,moduleName,parentId,level,roleId,packageName,imgName,initValue) values(65,'��������',1,2,1,'com.cneop.stoExpress.activity.scan.MsgNoticeActivity','dataquery','');");
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
			// ���ݿⲻ����
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

		sqlUpdateList.add("replace into tb_sysConfig (config_name,config_value,config_desc) values('MdControlIsOpen','1','�����浥ɨ��ܿع���')");

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

	// �ӵ�3�汾��ʼ,������ʼ����cusnote��isUrge
	private void updateTo3(List<String> sqlUpdateList, SQLiteDatabase db) {
		if (!checkColumnExist(db, "tb_order", "cusnote")) {
			sqlUpdateList.add(" alter table tb_order add cusnote varchar(50)  ");
		}
		if (!checkColumnExist(db, "tb_order", "isUrge")) {
			sqlUpdateList.add(" alter table tb_order add isUrge int  ");
		}

	}

	// 8�� ���κ��ս���İ�Ŀ�ĵط���������������������װ����������Ŀ�ĵ�װ������
	private void updateTo2(List<String> sqlUpdateList) {
		sqlUpdateList.add(" update tb_dic_module set state='D' WHERE Id=45 ");
		sqlUpdateList.add(" update tb_dic_module set state='D' WHERE Id=52 ");
		sqlUpdateList.add(" update tb_dic_module set state='D' WHERE Id=55 ");

	}

	/**
	 * ����1�����ĳ�����Ƿ����
	 * 
	 * @param db
	 * @param tableName
	 *            ����
	 * @param columnName
	 *            ����
	 * @return
	 */
	private boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			// ��ѯһ��
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
