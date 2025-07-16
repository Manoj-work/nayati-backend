package com.medhir.rest.controller.rbac;

import com.medhir.rest.config.rbac.MasterModulesConfig;
import com.medhir.rest.config.rbac.MasterModulesLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/master-modules")
@RequiredArgsConstructor
public class MasterModulesController {

    private final MasterModulesLoader loader;

    @GetMapping
    public MasterModulesConfig getMasterModules() {
        return loader.getConfig();
    }
}