INSERT INTO `CURRENCY` (
	`id`		,	`create_date`,	`last_modified_date`,
	`cname`		,	`ename`		 ,	`description`		,
	`eur_rate`	,	`gbp_rate`	 ,	`usd_rate`			
	
) VALUES (
  '1'			,CURRENT_TIMESTAMP	,CURRENT_TIMESTAMP,
  null			,'Bitcoin'			,'This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org',
  19329.0119	,16579.8224			,19842.0071
);