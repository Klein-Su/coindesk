DROP TABLE IF EXISTS `CURRENCY`;

-- 幣別匯率表
CREATE TABLE `CURRENCY` (
	`id`					BIGINT			PRIMARY KEY 				,	-- ID
	`create_date`			TIMESTAMP		DEFAULT CURRENT_TIMESTAMP	NOT NULL,	-- 建立時間
	`last_modified_date`	TIMESTAMP		NOT NULL					,	-- 最後修改時間
	`cname`					NVARCHAR(255)	NULL						,	-- 幣別中文代號
	`ename`					NVARCHAR(255)	NOT NULL					,	-- 幣別英文名稱
	`description`			NVARCHAR(255) 	NULL						,	-- 幣別描述
	`eur_rate`				numeric(20,4)	NOT NULL					,	-- 歐元匯率
	`gbp_rate`				numeric(20,4)	NOT NULL					,	-- 英鎊匯率
	`usd_rate`				numeric(20,4)	NOT NULL						-- 美元匯率
);