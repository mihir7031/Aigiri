package com.example.aigiri.repository

import com.example.aigiri.model.EmergencyContact

//class EmergencyContactsRepository {
//    suspend fun addEmergencyContact(userUid: String, contact: EmergencyContact): Result<Unit> {
//        return userDao.addEmergencyContact(userUid, contact)
//    }
//
//    suspend fun getEmergencyContacts(userUid: String): Result<List<EmergencyContact>> {
//        return userDao.getEmergencyContacts(userUid)
//    }
//
//    suspend fun getEmergencyContactIdByPhone(userUid: String, contactPhoneNo: String): Result<String?> {
//        return userDao.getEmergencyContactIdByPhone(userUid, contactPhoneNo)
//    }
//
//    suspend fun deleteEmergencyContact(userUid: String, contactDocId: String): Result<Unit> {
//        return userDao.deleteEmergencyContact(userUid, contactDocId)
//    }
//}