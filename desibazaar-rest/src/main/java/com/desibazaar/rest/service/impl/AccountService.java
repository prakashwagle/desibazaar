package com.desibazaar.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.desibazaar.rest.converter.DtoToEntityConverter;
import com.desibazaar.rest.converter.EntityToDtoConverter;
import com.desibazaar.rest.dao.IUserDao;
import com.desibazaar.rest.entity.EItem;
import com.desibazaar.rest.entity.EUser;
import com.desibazaar.rest.enums.Status;
import com.desibazaar.rest.service.IAccountService;
import com.desibazaar.rest.vo.Item;
import com.desibazaar.rest.vo.User;

/**
 * @author Varda Laud
 *
 */
@Service
@Transactional
public class AccountService implements IAccountService {
	private final static Logger LOGGER = Logger.getLogger(AccountService.class);

	@Autowired
	private IUserDao userDao;

	@Override
	public void createUser(User user) {
		user.setRating(0F);
		EUser eUser = DtoToEntityConverter.convertUserToEUser(user, null);
		getDao().createUser(eUser);
		LOGGER.debug("User created : " + user.getEmail());
	}

	@Override
	public void updateUser(User user) {
		EUser eUser = getDao().getUser(user.getEmail());
		DtoToEntityConverter.convertUserToEUser(user, eUser);
		getDao().updateUser(eUser);
		LOGGER.debug("User updated : " + user.getEmail());
	}

	@Override
	public User getUser(String email) {
		EUser eUser = getDao().getUser(email);
		return EntityToDtoConverter.convertEUserToUser(eUser);
	}

	@Override
	public List<Item> getSubscriptions(String email) {
		EUser eUser = getDao().getUser(email);
		List<EItem> eItems = new ArrayList<EItem>();
		for (EItem eItem : eUser.getSubscriptions()) {
			if (eItem.getStatus() == Status.Active
					|| eItem.getStatus() == Status.ToStart) {
				eItems.add(eItem);
			}
		}
		return EntityToDtoConverter.convertEItemToItem(eItems);
	}

	@Override
	public List<Item> getMyItems(String email) {
		EUser eUser = getDao().getUser(email);
		return EntityToDtoConverter.convertEItemToItem(eUser.getMyItems());
	}

	@Override
	public List<Item> getMyPurchases(String email) {
		EUser eUser = getDao().getUser(email);
		return EntityToDtoConverter.convertEItemToItem(eUser.getMyPurchases());
	}

	@Override
	public List<Item> getReviews(String email) {
		EUser eUser = getDao().getUser(email);
		return EntityToDtoConverter.convertEItemToItem(eUser.getMyItems());
	}

	@Override
	public boolean authenticate(User user) {
		EUser eUser;
		if ((eUser = getDao().getUser(user.getEmail())) != null) {
			if (eUser.getPassword().equals(user.getPassword())) {
				return true;
			}
		}
		return false;
	}

	private IUserDao getDao() {
		return userDao;
	}
}
