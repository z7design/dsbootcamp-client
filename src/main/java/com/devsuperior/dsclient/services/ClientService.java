package com.devsuperior.dsclient.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsclient.dto.ClientDTO;
import com.devsuperior.dsclient.entities.Client;
import com.devsuperior.dsclient.respositories.ClientRepository;
import com.devsuperior.dsclient.services.exceptions.DatabaseException;
import com.devsuperior.dsclient.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;

	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
		Page<Client> list = repository.findAll(pageRequest);
		return list.map(x -> new ClientDTO(x));
	}

	@Transactional(readOnly = true)
	public ClientDTO findById(Long id) {
		Optional<Client> obj = repository.findById(id);
		Client entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entidade não encontrada"));
		return new ClientDTO(entity);
	}

	@Transactional
	public ClientDTO insert(ClientDTO dto) {
		Client entity = new Client();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ClientDTO(entity);
	}

	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try {
			Client entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ClientDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Opss!! Id Não encontrado" + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Opss!! Id Não encontrado" + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de Integração");
		}
	}

	private void copyDtoToEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setCpf(dto.getCpf());
		entity.setIncome(dto.getIncome());
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());
	}
}
