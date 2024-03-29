
<!DOCTYPE html>
<!--
*  Pi-hole: A black hole for Internet advertisements
*  (c) 2017 Pi-hole, LLC (https://pi-hole.net)
*  Network-wide ad blocking via your own hardware.
*
*  This file is copyright under the latest version of the EUPL.
*  Please see LICENSE file for your rights under this license.
-->
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Usually browsers proactively perform domain name resolution on links that the user may choose to follow. We disable DNS prefetching here -->
    <meta http-equiv="x-dns-prefetch-control" content="off">
    <meta http-equiv="cache-control" content="max-age=60,private">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Pi-hole - bar-x-rabbit</title>

    <style>
        html { background-color: #000; }
    </style>
</head>
<body class="hold-transition layout-boxed login-page">
<div class="box login-box">
    <section style="padding: 15px;">
        <div class="login-logo">
            <div class="text-center">
                <img src="img/logo.svg" alt="Pi-hole logo" class="loginpage-logo">
            </div>
            <div class="panel-title text-center"><span class="logo-lg" style="font-size: 25px;">Pi-<b>hole</b></span></div>
        </div>
        <!-- /.login-logo -->

        <div class="card">
            <div class="card-body login-card-body">
                <div id="cookieInfo" class="panel-title text-center text-red" style="font-size: 150%" hidden>Verify that cookies are allowed for <code>192.168.68.60</code></div>

                <form action="" id="loginform" method="post">
                    <div class="form-group login-options has-feedback">
                        <div class="pwd-field">
                            <!-- hidden username input field to help password managers to autfill the password -->
                            <input type="text" id="username" value="pi.hole" autocomplete="username" hidden>
                            <input type="password" id="loginpw" name="pw" class="form-control" placeholder="Password" spellcheck="false" autocomplete="current-password" autofocus>
                            <span class="fa fa-key form-control-feedback"></span>
                        </div>
                        <div>
                            <input type="checkbox" id="logincookie" name="persistentlogin">
                            <label for="logincookie">Remember me for 7 days</label>
                        </div>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary form-control"><i class="fas fa-sign-in-alt"></i>&nbsp;&nbsp;&nbsp;Log in</button>
                    </div>
                </form>
                <br>
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box box-info collapsed-box">
                            <div class="box-header with-border pointer no-user-select" data-widget="collapse">
                                <h3 class="box-title">Forgot password?</h3>
                                <div class="box-tools pull-right">
                                    <button type="button" class="btn btn-box-tool">
                                        <i class="fa fa-plus"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="box-body">
                                <p>After installing Pi-hole for the first time, a password is generated and displayed
                                    to the user. The password cannot be retrieved later on, but it is possible to set
                                    a new password (or explicitly disable the password by setting an empty password)
                                    using the command
                                </p>
                                <pre>sudo pihole -a -p</pre>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.login-card-body -->
            <div class="login-footer" style="margin-top: 15px; display: flex; justify-content: space-between;">
                <a class="btn btn-default btn-sm" role="button" href="https://docs.pi-hole.net/" target="_blank"><i class="fas fa-question-circle"></i> Documentation</a>
                <a class="btn btn-default btn-sm" role="button" href="https://github.com/pi-hole/" target="_blank"><i class="fab fa-github"></i> Github</a>
                <a class="btn btn-default btn-sm" role="button" href="https://discourse.pi-hole.net/" target="_blank"><i class="fab fa-discourse"></i> Pi-hole Discourse</a>
            </div>
        </div>
    </section>
</div>

<div class="login-donate">
    <div class="text-center" style="font-size:125%">
        <strong><a href="https://pi-hole.net/donate/" rel="noopener" target="_blank"><i class="fa fa-heart text-red"></i> Donate</a></strong> if you found this useful.
    </div>
</div>
<script src="scripts/pi-hole/js/footer.js?v=1709587547"></script>
</body>
</html>
