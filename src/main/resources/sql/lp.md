selectLPByQQ
===
	select nickname,sum(dkp) dkp,sum(now_dkp) nowDkp,sum(use_dkp) useDkp from qys_users qu LEFT JOIN qys_users_info qui ON qu.info_id = qui.id where qq = #{qq}