package com.summer.melisma.service;

import com.summer.melisma.model.dto.MusicDto;
import com.summer.melisma.model.entity.MusicEntity;
import com.summer.melisma.model.entity.PlaylistEntity;
import com.summer.melisma.model.vo.MusicVo;
import com.summer.melisma.model.vo.PlaylistVo;
import com.summer.melisma.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MusicService {

    @Autowired
    MusicRepository musicRepository;

    @Autowired
    UserService userService;

    public MusicVo create(MusicDto dto){
        MusicEntity entity = MusicEntity.toEntity(dto);
        entity.setCreatedBy(userService.getUserId());
        musicRepository.save(entity);

        MusicVo vo = MusicVo.toVo(MusicDto.toDto(entity));

        return vo;
    }

    public List<MusicVo> searchList(){
        List<MusicEntity> entities = musicRepository.findAll();
        return entities.stream().map(entity -> MusicVo.toVo(MusicDto.toDto(entity))).collect(Collectors.toList());
    }

    public MusicVo search(UUID id){
        Optional<MusicEntity> entity = musicRepository.findById(id);

        if(entity.isPresent()) {
            return MusicVo.toVo(MusicDto.toDto(entity.get()));
        }else {
            throw new NullPointerException();
        }

    }

    public void delete(UUID id){
        musicRepository.deleteById(id);
    }

    public void update(MusicDto dto){
        musicRepository.findById(dto.getId()).ifPresentOrElse(musicEntity -> {
            MusicEntity newEntity = MusicEntity.toEntity(dto);
            musicEntity.setMusicUrl(newEntity.getMusicUrl());
            musicEntity.setViews(newEntity.getViews());
            musicEntity.setCreatedBy(newEntity.getCreatedBy());
            musicRepository.save(musicEntity);
        }, null);;
    }

    public void change(MusicDto dto){
        musicRepository.findById(dto.getId()).ifPresentOrElse(musicEntity -> {
            MusicEntity newEntity = MusicEntity.toEntity(dto);
            if(newEntity.getMusicUrl() != null){
                musicEntity.setMusicUrl(newEntity.getMusicUrl());
            }
            if(newEntity.getViews() != null){
                musicEntity.setViews(newEntity.getViews());
            }
            if(newEntity.getCreatedBy() != null){
                musicEntity.setCreatedBy(newEntity.getCreatedBy());
            }

            musicRepository.save(musicEntity);

        }, null );
    }

    public boolean isEmpty(UUID id){
        return musicRepository.findById(id).isEmpty();
    }
}
