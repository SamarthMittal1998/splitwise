package com.example.splitwise.controllers;

import com.example.splitwise.dto.BillRequestDto;
import com.example.splitwise.services.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/v1/bill")
public class BillController{

    @Autowired
    private BillService billServiceImpl;


    @PostMapping(value = "/group")
    public ResponseEntity<?> addNewBillToGroup(@RequestBody BillRequestDto billRequestDto) {

        billServiceImpl.addBillToGroup(billRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> addNewBill(@RequestBody BillRequestDto billRequestDto) {

        billServiceImpl.addBill(billRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{billId}")
    public ResponseEntity<BillRequestDto> getBill(@PathVariable(value = "billId") Long billId) {

        BillRequestDto billResponse = billServiceImpl.getBillDetails(billId);
        return new ResponseEntity<>(billResponse, HttpStatus.OK);
    }
}
