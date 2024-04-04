package com.ys.user.adapter.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/users",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserQueryController {
//    private final GetUserQuery getUserQuery;
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<ApiResponseModel<UserQueryModel>> get(@PathVariable("userId") int userId) {
//
//        UserQueryModel userQueryModel = getUserQuery.getById(userId);
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                ApiResponseModel.success(HttpStatus.OK.value(), userQueryModel));
//    }
//
//    @GetMapping("")
//    public ResponseEntity<ApiResponseModel<List<UserQueryModel>>> getAllByParams(GetAllUserQueryParams params) {
//
//        List<UserQueryModel> userQueryModelList = getUserQuery.getAllByParams(params);
//
//        if (!params.hasPagination()) {
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    ApiResponseModel.success(HttpStatus.OK.value(), userQueryModelList));
//        }
//
//        int totalCount = userQueryModelList.stream()
//                .findFirst()
//                .map(UserQueryModel::getTotalCount)
//                .orElse(0);
//        Pagination pagination = Pagination.create(params.getCurrentPage(), params.getPageSize(), totalCount);
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                ApiResponseModel.success(HttpStatus.OK.value(), userQueryModelList, pagination));
//    }
}
