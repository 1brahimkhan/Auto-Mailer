//package com.auto.mailer.dao;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.auto.mailer.bean.AutoMailerBean;
//
//@Repository
//public interface AutoMailerDao extends JpaRepository<AutoMailerBean, Long> {
//
//	boolean existsBytoAddress(String toAddress);

//	update db flag

//	@Modifying
//	@Transactional
//	@Query("UPDATE auto_mailer_bean a set a.isMailSent = :isSent where a.toAddress = :toAddress")
//	int updateDbFlag(@Param("toAddress") String toAddress, @Param("isSent") String isSent);

//}
